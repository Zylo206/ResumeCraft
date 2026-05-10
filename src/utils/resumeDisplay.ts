import type { ResumeModule } from '../api/resume'
import type { ResumeLanguage } from '../api/resume'
import { MODULE_LABELS, type BasicInfoContent, type ModuleType } from '../types'
import { normalizeBasicInfoContent } from './moduleContent'

const EN_MODULE_LABELS: Record<string, string> = {
  basic_info: 'Basic Information',
  education: 'Education',
  internship: 'Internship Experience',
  work_experience: 'Work Experience',
  project: 'Project Experience',
  skill: 'Skills',
  paper: 'Publications',
  research: 'Research Experience',
  award: 'Awards',
  job_intention: 'Career Objective',
}

export function normalizeResumeLanguage(language?: string | null): ResumeLanguage {
  return language === 'en-US' ? 'en-US' : 'zh-CN'
}

export function findBasicInfoContent(modules: ResumeModule[]): BasicInfoContent | null {
  const basicInfoModule = modules.find((module) => module.moduleType === 'basic_info')
  return basicInfoModule ? normalizeBasicInfoContent(basicInfoModule.content) : null
}

export function getModuleDisplayLabel(
  moduleType: ModuleType,
  _basicInfoContent?: Pick<BasicInfoContent, 'workYears'> | null,
  language?: ResumeLanguage
): string {
  if (normalizeResumeLanguage(language) === 'en-US') {
    return EN_MODULE_LABELS[moduleType] ?? MODULE_LABELS[moduleType]
  }
  return MODULE_LABELS[moduleType]
}

export function getModuleDisplayLabelFromModules(
  moduleType: ModuleType,
  modules: ResumeModule[],
  language?: ResumeLanguage
): string {
  return getModuleDisplayLabel(moduleType, findBasicInfoContent(modules), language)
}
