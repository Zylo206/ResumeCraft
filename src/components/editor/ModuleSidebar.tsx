import { useMemo, useState, type DragEvent } from 'react'
import type { ResumeModule } from '../../api/resume'
import { MODULE_ICONS, type ModuleType } from '../../types'
import { findBasicInfoContent, getModuleDisplayLabel } from '../../utils/resumeDisplay'

interface ModuleSidebarProps {
  modules: ResumeModule[]
  moduleTypeOrder: ModuleType[]
  activeModuleType: ModuleType | null
  onSelect: (moduleType: ModuleType) => void
  onAddModule: (moduleType: ModuleType) => void
  onRemoveModuleType: (moduleType: ModuleType) => void
  onReorderModules: (moduleIds: number[]) => Promise<void>
  onReorderModuleTypes: (moduleTypes: ModuleType[]) => void
  analysisActive?: boolean
  onSelectAnalysis?: () => void
  templateSelectionActive?: boolean
  onSelectTemplateSelection?: () => void
}

export const DEFAULT_MODULE_TYPE_ORDER: ModuleType[] = [
  'basic_info',
  'education',
  'internship',
  'work_experience',
  'project',
  'skill',
  'paper',
  'research',
  'award',
]

const NON_REMOVABLE_MODULE_TYPES = new Set<ModuleType>(['basic_info'])

function sortModules(modules: ResumeModule[]) {
  return [...modules].sort((a, b) => {
    if (a.sortOrder === b.sortOrder) {
      return a.id - b.id
    }

    return a.sortOrder - b.sortOrder
  })
}

export function ModuleSidebar({
  modules,
  moduleTypeOrder,
  activeModuleType,
  onSelect,
  onAddModule,
  onRemoveModuleType,
  onReorderModules,
  onReorderModuleTypes,
  analysisActive = false,
  onSelectAnalysis,
  templateSelectionActive = false,
  onSelectTemplateSelection,
}: ModuleSidebarProps) {
  const [draggedType, setDraggedType] = useState<ModuleType | null>(null)
  const [dragOverType, setDragOverType] = useState<ModuleType | null>(null)
  const [reordering, setReordering] = useState(false)
  const moduleViewActive = !analysisActive && !templateSelectionActive
  const basicInfoContent = findBasicInfoContent(modules)

  const { modulesByType, orderedRows } = useMemo(() => {
    const grouped = new Map<ModuleType, ResumeModule[]>()

    sortModules(modules).forEach((module) => {
      const moduleType = module.moduleType as ModuleType
      if (!DEFAULT_MODULE_TYPE_ORDER.includes(moduleType)) {
        return
      }

      if (!grouped.has(moduleType)) {
        grouped.set(moduleType, [])
      }
      grouped.get(moduleType)?.push(module)
    })

    return {
      modulesByType: grouped,
      orderedRows: moduleTypeOrder,
    }
  }, [modules, moduleTypeOrder])

  async function handleDrop(targetType: ModuleType) {
    if (!draggedType || draggedType === targetType) {
      setDraggedType(null)
      setDragOverType(null)
      return
    }

    const nextTypes = [...orderedRows]
    const fromIndex = nextTypes.indexOf(draggedType)
    const toIndex = nextTypes.indexOf(targetType)
    if (fromIndex < 0 || toIndex < 0) {
      setDraggedType(null)
      setDragOverType(null)
      return
    }

    const [movedType] = nextTypes.splice(fromIndex, 1)
    nextTypes.splice(toIndex, 0, movedType)
    onReorderModuleTypes(nextTypes)

    const moduleIds = nextTypes.flatMap((type) =>
      sortModules(modulesByType.get(type) ?? []).map((module) => module.id)
    )

    if (moduleIds.length <= 1) {
      setDraggedType(null)
      setDragOverType(null)
      return
    }

    setReordering(true)
    try {
      await onReorderModules(moduleIds)
    } finally {
      setReordering(false)
      setDraggedType(null)
      setDragOverType(null)
    }
  }

  function handleDragOver(event: DragEvent<HTMLDivElement>, targetType: ModuleType) {
    if (!draggedType || draggedType === targetType) {
      return
    }

    event.preventDefault()
    setDragOverType(targetType)
  }

  return (
    <aside className="sticky top-[65px] min-h-[calc(100vh-65px)] max-h-[calc(100vh-65px)] w-56 self-start overflow-y-auto border-r border-gray-200 bg-white">
      <div className="p-4">
        <div className="mb-3 flex items-center justify-between gap-2">
          <h2 className="text-sm font-semibold uppercase tracking-wider text-gray-400">模块</h2>
          {reordering && <span className="text-xs text-primary-600">保存中</span>}
        </div>
        <nav className="space-y-1">
          {orderedRows.map((type) => {
            const groupedModules = modulesByType.get(type) ?? []
            const exists = groupedModules.length > 0
            const isActive = moduleViewActive && activeModuleType === type
            const count = groupedModules.length
            const canRemove = exists && !NON_REMOVABLE_MODULE_TYPES.has(type)
            const moduleLabel = getModuleDisplayLabel(type, basicInfoContent)
            const isDragOver = dragOverType === type && draggedType !== type

            return (
              <div
                key={type}
                draggable={!reordering}
                onDragStart={() => {
                  setDraggedType(type)
                }}
                onDragOver={(event) => handleDragOver(event, type)}
                onDragLeave={() => {
                  if (dragOverType === type) {
                    setDragOverType(null)
                  }
                }}
                onDrop={() => void handleDrop(type)}
                onDragEnd={() => {
                  setDraggedType(null)
                  setDragOverType(null)
                }}
                className={`flex items-center gap-1 rounded-lg border border-transparent text-sm transition-colors ${
                  isActive
                    ? 'bg-primary-50 text-primary-700'
                    : 'text-gray-600 hover:bg-gray-50'
                } cursor-grab active:cursor-grabbing ${
                  draggedType === type ? 'opacity-60' : ''
                } ${
                  isDragOver ? 'border-primary-200 bg-primary-50/70' : ''
                }`}
              >
                <span
                  className="ml-2 flex h-7 w-5 shrink-0 items-center justify-center text-gray-300"
                  aria-hidden="true"
                >
                  <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7h.01M8 12h.01M8 17h.01M16 7h.01M16 12h.01M16 17h.01" />
                  </svg>
                </span>

                <button
                  type="button"
                  onClick={() => {
                    if (exists) {
                      onSelect(type)
                    } else {
                      onAddModule(type)
                    }
                  }}
                  className={`flex min-w-0 flex-1 items-center gap-2.5 rounded-lg py-2 pr-3 text-left ${
                    'pl-1'
                  } ${isActive ? 'font-medium' : ''}`}
                >
                  <span className="text-base">{MODULE_ICONS[type]}</span>
                  <span className="flex-1 truncate">{moduleLabel}</span>
                  {count > 1 && (
                    <span className="rounded bg-gray-100 px-1.5 py-0.5 text-xs text-gray-500">
                      {count}
                    </span>
                  )}
                </button>

                {canRemove ? (
                  <button
                    type="button"
                    onMouseDown={(event) => event.preventDefault()}
                    onClick={(event) => {
                      event.preventDefault()
                      event.stopPropagation()
                      onRemoveModuleType(type)
                    }}
                    className="mr-2 flex h-7 w-7 shrink-0 items-center justify-center rounded-md text-gray-300 transition-colors hover:bg-red-50 hover:text-red-500"
                    title={`删除${moduleLabel}`}
                    aria-label={`删除${moduleLabel}`}
                  >
                    <svg className="h-3.5 w-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                    </svg>
                  </button>
                ) : !exists ? (
                  <button
                    type="button"
                    onClick={() => onAddModule(type)}
                    className="mr-2 flex h-7 w-7 shrink-0 items-center justify-center rounded-md text-gray-300 transition-colors hover:bg-primary-50 hover:text-primary-600"
                    title={`添加${moduleLabel}`}
                    aria-label={`添加${moduleLabel}`}
                  >
                    <svg className="h-3.5 w-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                    </svg>
                  </button>
                ) : null}
              </div>
            )
          })}
        </nav>

        <div className="mt-6 border-t border-gray-100 pt-4">
          <h2 className="mb-3 text-sm font-semibold uppercase tracking-wider text-gray-400">分析</h2>
          <button
            type="button"
            onClick={onSelectAnalysis}
            className={`w-full rounded-lg px-3 py-2 text-left text-sm transition-colors ${
              analysisActive
                ? 'bg-primary-50 font-medium text-primary-700'
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <span className="flex items-center gap-2.5">
              <span className="text-base">📊</span>
              <span className="flex-1">简历分析</span>
            </span>
          </button>
        </div>

        <div className="mt-6 border-t border-gray-100 pt-4">
          <h2 className="mb-3 text-sm font-semibold uppercase tracking-wider text-gray-400">导出</h2>
          <button
            type="button"
            onClick={onSelectTemplateSelection}
            className={`w-full rounded-lg px-3 py-2 text-left text-sm transition-colors ${
              templateSelectionActive
                ? 'bg-primary-50 font-medium text-primary-700'
                : 'text-gray-600 hover:bg-gray-50'
            }`}
          >
            <span className="flex items-center gap-2.5">
              <span className="text-base">🖨️</span>
              <span className="flex-1">预览与导出</span>
            </span>
          </button>
        </div>
      </div>
    </aside>
  )
}
