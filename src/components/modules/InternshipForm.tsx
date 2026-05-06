import { ExperienceModuleForm } from './ExperienceModuleForm'

interface Props {
  resumeId: number
  moduleId: number
  initialContent: Record<string, unknown>
}

export function InternshipForm({ resumeId, moduleId, initialContent }: Props) {
  return (
    <ExperienceModuleForm
      resumeId={resumeId}
      moduleId={moduleId}
      initialContent={initialContent}
      moduleType="internship"
      moduleLabel="实习经历"
      summaryPlaceholder="填写这段实习项目的背景、目标和整体内容"
    />
  )
}
