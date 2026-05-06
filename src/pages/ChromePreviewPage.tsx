import { useParams, useSearchParams } from 'react-router-dom'
import { ResumePdfPreviewSurface } from '../components/editor/ResumePdfPreviewSurface'
import {
  resolveResumePdfAccentPreset,
  resolveResumePdfDensity,
  resolveResumePdfHeadingStyle,
  resolveResumePdfTemplateId,
  type ResumePdfAccentPreset,
  type ResumePdfDensity,
  type ResumePdfHeadingStyle,
  type ResumePdfPageMode,
  type ResumePdfTemplateId,
} from '../utils/resumePdf'

export default function ChromePreviewPage() {
  const { id } = useParams<{ id: string }>()
  const [searchParams] = useSearchParams()
  const resumeId = Number(id)
  const refreshToken = searchParams.get('refresh') ?? ''
  const pageMode: ResumePdfPageMode = searchParams.get('pageMode') === 'continuous'
    ? 'continuous'
    : 'standard'
  const templateId: ResumePdfTemplateId = resolveResumePdfTemplateId(searchParams.get('templateId'))
  const densityParam = searchParams.get('density')
  const accentPresetParam = searchParams.get('accentPreset')
  const headingStyleParam = searchParams.get('headingStyle')
  const density: ResumePdfDensity | undefined = densityParam ? resolveResumePdfDensity(densityParam) : undefined
  const accentPreset: ResumePdfAccentPreset | undefined = accentPresetParam ? resolveResumePdfAccentPreset(accentPresetParam) : undefined
  const headingStyle: ResumePdfHeadingStyle | undefined = headingStyleParam ? resolveResumePdfHeadingStyle(headingStyleParam) : undefined

  return (
    <ResumePdfPreviewSurface
      resumeId={resumeId}
      pageMode={pageMode}
      templateId={templateId}
      density={density}
      accentPreset={accentPreset}
      headingStyle={headingStyle}
      refreshToken={refreshToken}
    />
  )
}
