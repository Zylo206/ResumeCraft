import { create } from 'zustand'
import { resumeApi, type ResumeListItem, type ResumeModule, type ResumeLanguage } from '../api/resume'
import type { ImportedResumeData } from '../utils/importers'

function getSortTime(value: string): number {
  const time = new Date(value).getTime()
  return Number.isNaN(time) ? 0 : time
}

function sortResumeList(resumeList: ResumeListItem[]): ResumeListItem[] {
  return [...resumeList].sort((a, b) => {
    const createdAtDiff = getSortTime(b.createdAt) - getSortTime(a.createdAt)
    if (createdAtDiff !== 0) {
      return createdAtDiff
    }

    return b.id - a.id
  })
}

function sortModules(modules: ResumeModule[]): ResumeModule[] {
  return [...modules].sort((a, b) => {
    if (a.sortOrder === b.sortOrder) {
      return a.id - b.id
    }

    return a.sortOrder - b.sortOrder
  })
}

interface ResumeState {
  resumeList: ResumeListItem[]
  currentResumeId: number | null
  modules: ResumeModule[]
  loading: boolean
  fetchResumeList: () => Promise<void>
  createResume: (title?: string, language?: ResumeLanguage) => Promise<ResumeListItem>
  importResume: (payload: ImportedResumeData) => Promise<ResumeListItem>
  renameResume: (id: number, title: string) => Promise<ResumeListItem>
  updateResumeLanguage: (id: number, language: ResumeLanguage) => Promise<ResumeListItem>
  deleteResume: (id: number) => Promise<void>
  fetchModules: (resumeId: number) => Promise<void>
  updateModuleContent: (resumeId: number, moduleId: number, content: Record<string, unknown>) => Promise<void>
  addModule: (resumeId: number, moduleType: string, content: Record<string, unknown>, sortOrder?: number) => Promise<ResumeModule>
  reorderModules: (resumeId: number, moduleIds: number[]) => Promise<void>
  deleteModule: (resumeId: number, moduleId: number) => Promise<void>
  setCurrentResumeId: (id: number | null) => void
}

export const useResumeStore = create<ResumeState>((set) => ({
  resumeList: [],
  currentResumeId: null,
  modules: [],
  loading: false,

  fetchResumeList: async () => {
    set({ loading: true })
    try {
      const { data: res } = await resumeApi.list()
      set({ resumeList: sortResumeList(res.data), loading: false })
    } catch {
      set({ loading: false })
    }
  },

  createResume: async (title, language) => {
    const { data: res } = await resumeApi.create({ title, language })
    const newResume = res.data
    set((state) => ({ resumeList: sortResumeList([newResume, ...state.resumeList]) }))
    return newResume
  },

  importResume: async ({ title, modules }) => {
    const { data: resumeRes } = await resumeApi.create({ title })
    const newResume = resumeRes.data

    for (const [index, module] of modules.entries()) {
      await resumeApi.addModule(newResume.id, {
        moduleType: module.moduleType,
        content: module.content,
        sortOrder: index,
      })
    }

    set((state) => ({ resumeList: sortResumeList([newResume, ...state.resumeList]) }))
    return newResume
  },

  renameResume: async (id, title) => {
    const { data: res } = await resumeApi.update(id, { title })
    const updatedResume = res.data
    set((state) => ({
      resumeList: state.resumeList.map((resume) =>
        resume.id === id ? updatedResume : resume
      ),
    }))
    return updatedResume
  },

  updateResumeLanguage: async (id, language) => {
    const { data: res } = await resumeApi.update(id, { language })
    const updatedResume = res.data
    set((state) => ({
      resumeList: state.resumeList.map((resume) =>
        resume.id === id ? updatedResume : resume
      ),
    }))
    return updatedResume
  },

  deleteResume: async (id) => {
    await resumeApi.delete(id)
    set((state) => ({
      resumeList: state.resumeList.filter((r) => r.id !== id),
      currentResumeId: state.currentResumeId === id ? null : state.currentResumeId,
    }))
  },

  fetchModules: async (resumeId) => {
    set({ loading: true, currentResumeId: resumeId, modules: [] })
    try {
      const { data: res } = await resumeApi.getModules(resumeId)
      set({ modules: sortModules(res.data), loading: false })
    } catch {
      set({ loading: false })
    }
  },

  updateModuleContent: async (resumeId, moduleId, content) => {
    const { data: res } = await resumeApi.updateModule(resumeId, moduleId, content)
    set((state) => ({
      modules: state.currentResumeId === resumeId
        ? state.modules.map((m) =>
            m.id === moduleId ? res.data : m
          )
        : state.modules,
    }))
  },

  addModule: async (resumeId, moduleType, content, sortOrder) => {
    const { data: res } = await resumeApi.addModule(resumeId, { moduleType, content, sortOrder })
    set((state) => ({
      modules: state.currentResumeId === resumeId
        ? sortModules([...state.modules, res.data])
        : state.modules,
    }))
    return res.data
  },

  reorderModules: async (resumeId, moduleIds) => {
    const { data: res } = await resumeApi.reorderModules(resumeId, moduleIds)
    set((state) => ({
      modules: state.currentResumeId === resumeId
        ? sortModules(res.data)
        : state.modules,
    }))
  },

  deleteModule: async (resumeId, moduleId) => {
    await resumeApi.deleteModule(resumeId, moduleId)
    set((state) => ({
      modules: state.currentResumeId === resumeId
        ? state.modules.filter((m) => m.id !== moduleId)
        : state.modules,
    }))
  },

  setCurrentResumeId: (id) => set({ currentResumeId: id }),
}))
