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

// ── Date formatting ──

const EN_MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

export function formatMonth(value: string, language?: ResumeLanguage): string {
  if (!value) return ''
  const [year, month] = value.split('-')
  if (!year || !month) return value
  if (normalizeResumeLanguage(language) === 'en-US') {
    return `${EN_MONTHS[Number(month) - 1]} ${year}`
  }
  return `${year}年-${Number(month)}月`
}

export function formatMonthRange(start: string, end: string, language?: ResumeLanguage): string {
  const startText = formatMonth(start, language)
  const endText = formatMonth(end, language)
  if (startText && endText) {
    return normalizeResumeLanguage(language) === 'en-US'
      ? `${startText} - ${endText}`
      : `${startText}至${endText}`
  }
  return startText || endText
}

export function formatAwardDisplayTime(value: string, language?: ResumeLanguage): string {
  if (!value) return ''
  const [year] = value.split('-')
  if (!year) return value
  return normalizeResumeLanguage(language) === 'en-US' ? year : `${year}年`
}

// ── UI label helpers ──

type UILabelKey =
  | 'sidebarTitle'
  | 'previewTitle'
  | 'exportPdf'
  | 'saving'
  | 'saved'
  | 'saveFailed'
  | 'addModule'
  | 'deleteModule'
  | 'moveUp'
  | 'moveDown'
  | 'aiOptimize'
  | 'aiAnalyzing'
  | 'noModules'
  | 'loading'
  | 'confirmDelete'
  | 'cancel'
  | 'confirm'
  | 'edit'
  | 'delete'
  | 'name'
  | 'email'
  | 'phone'
  | 'gender'
  | 'birthDate'
  | 'location'
  | 'politicalStatus'
  | 'ethnicity'
  | 'website'
  | 'objective'
  | 'targetPosition'
  | 'targetCity'
  | 'expectedSalary'
  | 'industry'
  | 'education'
  | 'school'
  | 'major'
  | 'degree'
  | 'gpa'
  | 'ranking'
  | 'startDate'
  | 'endDate'
  | 'description'
  | 'company'
  | 'position'
  | 'responsibilities'
  | 'techStack'
  | 'projectName'
  | 'projectRole'
  | 'projectLink'
  | 'projectSummary'
  | 'coreDuties'
  | 'skills'
  | 'skillName'
  | 'proficiency'
  | 'paperTitle'
  | 'journal'
  | 'publishDate'
  | 'authors'
  | 'doi'
  | 'researchTitle'
  | 'researchRole'
  | 'organization'
  | 'awardName'
  | 'awardDate'
  | 'issuer'
  | 'generateEnglishCopy'
  | 'generateEnglishCopyTooltip'
  | 'generating'
  | 'translateSuccess'
  | 'translateFailed'
  | 'present'

const UI_LABELS: Record<UILabelKey, { 'zh-CN': string; 'en-US': string }> = {
  sidebarTitle: { 'zh-CN': '模块列表', 'en-US': 'Modules' },
  previewTitle: { 'zh-CN': '实时预览', 'en-US': 'Preview' },
  exportPdf: { 'zh-CN': '导出 PDF', 'en-US': 'Export PDF' },
  saving: { 'zh-CN': '保存中…', 'en-US': 'Saving…' },
  saved: { 'zh-CN': '已保存', 'en-US': 'Saved' },
  saveFailed: { 'zh-CN': '保存失败', 'en-US': 'Save failed' },
  addModule: { 'zh-CN': '添加模块', 'en-US': 'Add Module' },
  deleteModule: { 'zh-CN': '删除模块', 'en-US': 'Delete Module' },
  moveUp: { 'zh-CN': '上移', 'en-US': 'Move Up' },
  moveDown: { 'zh-CN': '下移', 'en-US': 'Move Down' },
  aiOptimize: { 'zh-CN': 'AI 优化', 'en-US': 'AI Optimize' },
  aiAnalyzing: { 'zh-CN': 'AI 分析中…', 'en-US': 'AI Analyzing…' },
  noModules: { 'zh-CN': '暂无模块', 'en-US': 'No modules yet' },
  loading: { 'zh-CN': '加载中…', 'en-US': 'Loading…' },
  confirmDelete: { 'zh-CN': '确认删除？', 'en-US': 'Confirm delete?' },
  cancel: { 'zh-CN': '取消', 'en-US': 'Cancel' },
  confirm: { 'zh-CN': '确认', 'en-US': 'Confirm' },
  edit: { 'zh-CN': '编辑', 'en-US': 'Edit' },
  delete: { 'zh-CN': '删除', 'en-US': 'Delete' },
  name: { 'zh-CN': '姓名', 'en-US': 'Name' },
  email: { 'zh-CN': '邮箱', 'en-US': 'Email' },
  phone: { 'zh-CN': '手机号', 'en-US': 'Phone' },
  gender: { 'zh-CN': '性别', 'en-US': 'Gender' },
  birthDate: { 'zh-CN': '出生日期', 'en-US': 'Date of Birth' },
  location: { 'zh-CN': '所在地', 'en-US': 'Location' },
  politicalStatus: { 'zh-CN': '政治面貌', 'en-US': 'Political Status' },
  ethnicity: { 'zh-CN': '民族', 'en-US': 'Ethnicity' },
  website: { 'zh-CN': '个人网站', 'en-US': 'Website' },
  objective: { 'zh-CN': '求职意向', 'en-US': 'Career Objective' },
  targetPosition: { 'zh-CN': '期望岗位', 'en-US': 'Target Position' },
  targetCity: { 'zh-CN': '期望城市', 'en-US': 'Target City' },
  expectedSalary: { 'zh-CN': '期望薪资', 'en-US': 'Expected Salary' },
  industry: { 'zh-CN': '期望行业', 'en-US': 'Target Industry' },
  education: { 'zh-CN': '教育经历', 'en-US': 'Education' },
  school: { 'zh-CN': '学校', 'en-US': 'School' },
  major: { 'zh-CN': '专业', 'en-US': 'Major' },
  degree: { 'zh-CN': '学历', 'en-US': 'Degree' },
  gpa: { 'zh-CN': 'GPA', 'en-US': 'GPA' },
  ranking: { 'zh-CN': '排名', 'en-US': 'Ranking' },
  startDate: { 'zh-CN': '开始时间', 'en-US': 'Start Date' },
  endDate: { 'zh-CN': '结束时间', 'en-US': 'End Date' },
  description: { 'zh-CN': '描述', 'en-US': 'Description' },
  company: { 'zh-CN': '公司', 'en-US': 'Company' },
  position: { 'zh-CN': '岗位', 'en-US': 'Position' },
  responsibilities: { 'zh-CN': '职责描述', 'en-US': 'Responsibilities' },
  techStack: { 'zh-CN': '技术栈', 'en-US': 'Tech Stack' },
  projectName: { 'zh-CN': '项目名称', 'en-US': 'Project Name' },
  projectRole: { 'zh-CN': '担任角色', 'en-US': 'Role' },
  projectLink: { 'zh-CN': '项目链接', 'en-US': 'Project Link' },
  projectSummary: { 'zh-CN': '项目简介', 'en-US': 'Project Summary' },
  coreDuties: { 'zh-CN': '核心职责', 'en-US': 'Core Responsibilities' },
  skills: { 'zh-CN': '技能', 'en-US': 'Skills' },
  skillName: { 'zh-CN': '技能名称', 'en-US': 'Skill Name' },
  proficiency: { 'zh-CN': '熟练程度', 'en-US': 'Proficiency' },
  paperTitle: { 'zh-CN': '论文标题', 'en-US': 'Paper Title' },
  journal: { 'zh-CN': '期刊/会议', 'en-US': 'Journal / Conference' },
  publishDate: { 'zh-CN': '发表时间', 'en-US': 'Publication Date' },
  authors: { 'zh-CN': '作者', 'en-US': 'Authors' },
  doi: { 'zh-CN': 'DOI', 'en-US': 'DOI' },
  researchTitle: { 'zh-CN': '研究课题', 'en-US': 'Research Title' },
  researchRole: { 'zh-CN': '担任角色', 'en-US': 'Role' },
  organization: { 'zh-CN': '研究机构', 'en-US': 'Organization' },
  awardName: { 'zh-CN': '奖项名称', 'en-US': 'Award Name' },
  awardDate: { 'zh-CN': '获奖时间', 'en-US': 'Award Date' },
  issuer: { 'zh-CN': '颁发机构', 'en-US': 'Issuer' },
  generateEnglishCopy: { 'zh-CN': '生成英文版简历', 'en-US': 'Generate English Copy' },
  generateEnglishCopyTooltip: {
    'zh-CN': '将当前简历复制并转换为英文简历，原简历不会被覆盖。',
    'en-US': 'Create an English copy of this resume. The original will not be modified.',
  },
  generating: { 'zh-CN': '生成中…', 'en-US': 'Generating…' },
  translateSuccess: { 'zh-CN': '英文简历生成成功', 'en-US': 'English resume generated' },
  translateFailed: { 'zh-CN': '英文简历生成失败', 'en-US': 'Failed to generate English resume' },
  present: { 'zh-CN': '至今', 'en-US': 'Present' },
}

export function getUILabel(key: UILabelKey, language?: ResumeLanguage): string {
  const lang = normalizeResumeLanguage(language)
  return UI_LABELS[key]?.[lang] ?? UI_LABELS[key]?.['zh-CN'] ?? key
}

export type { UILabelKey }
