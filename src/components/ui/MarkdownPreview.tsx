import ReactMarkdown from 'react-markdown'
import remarkBreaks from 'remark-breaks'
import remarkGfm from 'remark-gfm'

interface MarkdownPreviewProps {
  content: string
  emptyText: string
  className?: string
}

function joinClasses(...values: Array<string | undefined | false>) {
  return values.filter(Boolean).join(' ')
}

function normalizeMarkdownContent(content: string) {
  const lines = content.replace(/\r\n/g, '\n').split('\n')
  const normalized: string[] = []

  for (const line of lines) {
    const withStrongLabelSpacing = line.replace(/(\*\*[^*\n]+?\*\*)(?=\S)/g, '$1 ')
    const trimmed = withStrongLabelSpacing.trim()
    const isStrongLabelLine = /^\*\*[^*\n]+?\*\*.+$/.test(trimmed)

    if (isStrongLabelLine && normalized.length > 0 && normalized[normalized.length - 1].trim() !== '') {
      normalized.push('')
    }

    normalized.push(withStrongLabelSpacing)
  }

  return normalized.join('\n')
}

export function MarkdownPreview({ content, emptyText, className }: MarkdownPreviewProps) {
  const hasContent = content.trim().length > 0
  const normalizedContent = hasContent ? normalizeMarkdownContent(content) : ''

  return (
    <div className={joinClasses('min-w-0 max-w-full overflow-auto rounded-2xl border p-4', className)}>
      {hasContent ? (
        <div className="min-w-0 max-w-full space-y-3 break-words text-sm leading-7 text-slate-700">
          <ReactMarkdown
            remarkPlugins={[remarkGfm, remarkBreaks]}
            components={{
              h1: ({ children }) => <h1 className="text-base font-semibold text-slate-900">{children}</h1>,
              h2: ({ children }) => <h2 className="text-sm font-semibold text-slate-900">{children}</h2>,
              h3: ({ children }) => <h3 className="text-sm font-medium text-slate-900">{children}</h3>,
              p: ({ children }) => <p className="m-0">{children}</p>,
              strong: ({ children }) => <strong className="font-semibold text-slate-900">{children}</strong>,
              em: ({ children }) => <em className="italic text-slate-800">{children}</em>,
              ul: ({ children }) => <ul className="m-0 list-disc space-y-2 pl-5">{children}</ul>,
              ol: ({ children }) => <ol className="m-0 list-decimal space-y-2 pl-5">{children}</ol>,
              li: ({ children }) => <li className="pl-1">{children}</li>,
              blockquote: ({ children }) => (
                <blockquote className="m-0 border-l-2 border-primary-200 bg-primary-50/60 px-3 py-2 text-slate-600">
                  {children}
                </blockquote>
              ),
              a: ({ children, href }) => (
                <a
                  href={href}
                  target="_blank"
                  rel="noreferrer"
                  className="text-primary-700 underline decoration-primary-200 underline-offset-2"
                >
                  {children}
                </a>
              ),
              code: ({ children }) => (
                <code className="max-w-full break-all rounded bg-slate-100 px-1.5 py-0.5 font-mono text-[0.9em] text-slate-800">
                  {children}
                </code>
              ),
              pre: ({ children }) => (
                <pre className="m-0 min-w-0 max-w-full overflow-x-auto whitespace-pre-wrap break-all rounded-xl bg-slate-950 p-4 text-xs leading-6 text-slate-50 [&_code]:rounded-none [&_code]:bg-transparent [&_code]:px-0 [&_code]:py-0 [&_code]:text-inherit">
                  {children}
                </pre>
              ),
              hr: () => <hr className="border-slate-200" />,
              table: ({ children }) => (
                <div className="overflow-auto rounded-xl border border-slate-200">
                  <table className="min-w-full border-collapse text-left text-sm">{children}</table>
                </div>
              ),
              thead: ({ children }) => <thead className="bg-slate-50 text-slate-700">{children}</thead>,
              th: ({ children }) => <th className="border-b border-slate-200 px-3 py-2 font-medium">{children}</th>,
              td: ({ children }) => <td className="border-b border-slate-100 px-3 py-2 align-top">{children}</td>,
            }}
          >
            {normalizedContent}
          </ReactMarkdown>
        </div>
      ) : (
        <div className="text-sm leading-7 text-slate-500">{emptyText}</div>
      )}
    </div>
  )
}
