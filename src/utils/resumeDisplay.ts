import type { ResumeModule } from '../api/resume'
import { MODULE_LABELS, type BasicInfoContent, type ModuleType } from '../types'
import { normalizeBasicInfoContent } from './moduleContent'

export function findBasicInfoContent(modules: ResumeModule[]): BasicInfoContent | null {
  const basicInfoModule = modules.find((module) => module.moduleType === 'basic_info')
  return basicInfoModule ? normalizeBasicInfoContent(basicInfoModule.content) : null
}

export function getModuleDisplayLabel(
  moduleType: ModuleType,
  _basicInfoContent?: Pick<BasicInfoContent, 'workYears'> | null
): string {
  return MODULE_LABELS[moduleType]
}

export function getModuleDisplayLabelFromModules(moduleType: ModuleType, modules: ResumeModule[]): string {
  return getModuleDisplayLabel(moduleType, findBasicInfoContent(modules))
}
