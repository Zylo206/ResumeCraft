import { useEffect, useMemo, useRef, useState } from 'react'
import * as PDFJS from 'pdfjs-dist'
import pdfWorkerUrl from 'pdfjs-dist/build/pdf.worker.min.mjs?url'
import { useResumeStore } from '../../store/resumeStore'
import {
  generateResumePdfPreviewAsset,
  type ResumePdfAccentPreset,
  type ResumePdfDensity,
  type ResumePdfHeadingStyle,
  type ResumePdfPageMode,
  type ResumePdfPreviewMeta,
  type ResumePdfTemplateId,
} from '../../utils/resumePdf'

const STANDARD_PAGE_GAP_PX = 24
const PREVIEW_VERTICAL_BUFFER_PX = 32
const FALLBACK_STANDARD_PREVIEW_HEIGHT_PX = 1160
const FALLBACK_CONTINUOUS_PREVIEW_HEIGHT_PX = 760

interface ResumePdfPreviewSurfaceProps {
  resumeId: number
  pageMode: ResumePdfPageMode
  templateId: ResumePdfTemplateId
  density?: ResumePdfDensity
  accentPreset?: ResumePdfAccentPreset
  headingStyle?: ResumePdfHeadingStyle
  refreshToken?: string
}

interface RenderedPdfPage {
  pageNumber: number
  width: number
  height: number
  dataUrl: string
}

PDFJS.GlobalWorkerOptions.workerSrc = pdfWorkerUrl

export function ResumePdfPreviewSurface({
  resumeId,
  pageMode,
  templateId,
  density,
  accentPreset,
  headingStyle,
  refreshToken = '',
}: ResumePdfPreviewSurfaceProps) {
  const { modules, loading, fetchModules, resumeList } = useResumeStore()
  const currentResume = resumeList.find((resume) => resume.id === resumeId)
  const resumeLanguage = currentResume?.language === 'en-US' ? 'en-US' : 'zh-CN'
  const [pdfBlob, setPdfBlob] = useState<Blob | null>(null)
  const [previewMeta, setPreviewMeta] = useState<ResumePdfPreviewMeta | null>(null)
  const [pdfLoading, setPdfLoading] = useState(false)
  const [pdfError, setPdfError] = useState('')
  const [renderedPages, setRenderedPages] = useState<RenderedPdfPage[]>([])
  const [rendering, setRendering] = useState(false)
  const [containerWidth, setContainerWidth] = useState(0)
  const requestIdRef = useRef(0)
  const containerRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!resumeId) {
      return
    }

    void fetchModules(resumeId)
  }, [fetchModules, resumeId])

  useEffect(() => {
    if (modules.length === 0) {
      return
    }

    let cancelled = false
    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId
    setPdfLoading(true)
    setPdfError('')

    void generateResumePdfPreviewAsset(modules, { pageMode, templateId, density, accentPreset, headingStyle, language: resumeLanguage })
      .then(({ blob, previewMeta: nextPreviewMeta }) => {
        if (cancelled || requestId !== requestIdRef.current) {
          return
        }

        setPdfBlob(blob)
        setPreviewMeta(nextPreviewMeta)
        setPdfLoading(false)
      })
      .catch((error: unknown) => {
        if (cancelled || requestId !== requestIdRef.current) {
          return
        }

        setPdfBlob(null)
        setPreviewMeta(null)
        setPdfLoading(false)
        setPdfError(error instanceof Error ? error.message : 'PDF 预览生成失败')
      })

    return () => {
      cancelled = true
    }
  }, [accentPreset, density, headingStyle, modules, pageMode, refreshToken, resumeLanguage, templateId])

  useEffect(() => {
    const element = containerRef.current
    if (!element || typeof ResizeObserver === 'undefined') {
      return
    }

    const observer = new ResizeObserver((entries) => {
      const entry = entries[0]
      if (!entry) {
        return
      }

      setContainerWidth(Math.floor(entry.contentRect.width))
    })

    observer.observe(element)
    setContainerWidth(Math.floor(element.getBoundingClientRect().width))

    return () => observer.disconnect()
  }, [])

  const previewTitle = useMemo(
    () => (pageMode === 'continuous' ? '简历模板预览 - 智能一页' : '简历模板预览 - 标准 PDF'),
    [pageMode]
  )

  const previewHeight = useMemo(() => {
    if (!previewMeta) {
      return pageMode === 'standard'
        ? FALLBACK_STANDARD_PREVIEW_HEIGHT_PX
        : FALLBACK_CONTINUOUS_PREVIEW_HEIGHT_PX
    }

    const effectiveContainerWidth = containerWidth > 0 ? containerWidth : window.innerWidth
    const effectivePageWidth = previewMeta.pageWidth > 0 ? previewMeta.pageWidth : 595.28
    const scaledPageHeights = previewMeta.pageHeights.map((pageHeight) => (
      effectiveContainerWidth * pageHeight / effectivePageWidth
    ))
    const totalPageHeight = scaledPageHeights.reduce((sum, pageHeight) => sum + pageHeight, 0)
    const pageGap = pageMode === 'standard' && previewMeta.pageCount > 1
      ? STANDARD_PAGE_GAP_PX * (previewMeta.pageCount - 1)
      : 0

    return Math.ceil(totalPageHeight + pageGap + PREVIEW_VERTICAL_BUFFER_PX)
  }, [containerWidth, pageMode, previewMeta])

  useEffect(() => {
    if (!pdfBlob || containerWidth <= 0) {
      setRenderedPages([])
      setRendering(false)
      return
    }

    let cancelled = false
    setRendering(true)

    void (async () => {
      try {
        const arrayBuffer = await pdfBlob.arrayBuffer()
        const pdf = await PDFJS.getDocument({ data: arrayBuffer }).promise
        const nextPages: RenderedPdfPage[] = []

        for (let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber += 1) {
          const page = await pdf.getPage(pageNumber)
          const baseViewport = page.getViewport({ scale: 1 })
          const scale = containerWidth / baseViewport.width
          const viewport = page.getViewport({ scale })
          const outputScale = window.devicePixelRatio || 1

          const canvas = document.createElement('canvas')
          const context = canvas.getContext('2d')
          if (!context) {
            throw new Error('PDF 预览上下文创建失败')
          }

          canvas.width = Math.floor(viewport.width * outputScale)
          canvas.height = Math.floor(viewport.height * outputScale)
          canvas.style.width = `${viewport.width}px`
          canvas.style.height = `${viewport.height}px`

          await page.render({
            canvasContext: context,
            viewport,
            transform: outputScale === 1 ? undefined : [outputScale, 0, 0, outputScale, 0, 0],
          }).promise

          nextPages.push({
            pageNumber,
            width: viewport.width,
            height: viewport.height,
            dataUrl: canvas.toDataURL('image/png'),
          })
        }

        if (cancelled) {
          return
        }

        setRenderedPages(nextPages)
        setRendering(false)
      } catch (error) {
        if (cancelled) {
          return
        }

        setRenderedPages([])
        setRendering(false)
        setPdfError(error instanceof Error ? error.message : 'PDF 预览渲染失败')
      }
    })()

    return () => {
      cancelled = true
    }
  }, [containerWidth, pdfBlob])

  return (
    <div ref={containerRef} className="w-full overflow-hidden bg-white" style={{ minHeight: `${previewHeight}px` }}>
      {loading ? (
        <div className="flex min-h-[520px] items-center justify-center text-gray-400">
          正在加载简历...
        </div>
      ) : modules.length === 0 ? (
        <div className="flex min-h-[520px] flex-col items-center justify-center gap-2 p-6 text-center text-sm text-gray-400">
          <p>暂无简历模块内容</p>
          <p className="text-xs text-gray-300">请先在编辑页面添加简历模块后再预览。</p>
        </div>
      ) : pdfError ? (
        <div className="flex min-h-[520px] items-center justify-center p-6">
          <div className="rounded-2xl border border-red-200 bg-red-50 px-5 py-4 text-sm text-red-700">
            {pdfError}
          </div>
        </div>
      ) : !pdfBlob || pdfLoading || rendering ? (
        <div className="flex min-h-[520px] items-center justify-center text-gray-400">
          {rendering ? '正在渲染模板预览...' : '正在生成模板预览...'}
        </div>
      ) : (
        <div>
          {renderedPages.map((page, index) => (
            <div key={page.pageNumber}>
              {pageMode === 'standard' && index > 0 && renderedPages.length > 1 && (
                <div className="my-6 flex items-center gap-3 text-xs text-slate-400">
                  <div className="h-px flex-1 border-t border-dashed border-slate-300" />
                  <span className="shrink-0 rounded-full bg-slate-100 px-3 py-1 text-slate-500">
                    第 {page.pageNumber - 1} 页结束 / 第 {page.pageNumber} 页开始
                  </span>
                  <div className="h-px flex-1 border-t border-dashed border-slate-300" />
                </div>
              )}

              <figure className="relative mx-auto overflow-hidden bg-white shadow-sm ring-1 ring-slate-200">
                {pageMode === 'standard' && (
                  <div className="absolute right-3 top-3 z-10 rounded-full bg-white/90 px-2.5 py-1 text-xs text-slate-500 shadow-sm ring-1 ring-slate-200">
                    第 {page.pageNumber} 页
                  </div>
                )}

                <img
                  src={page.dataUrl}
                  alt={`${previewTitle} 第 ${page.pageNumber} 页`}
                  width={Math.round(page.width)}
                  height={Math.round(page.height)}
                  className="block h-auto w-full"
                />
              </figure>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
