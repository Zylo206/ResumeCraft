import { useEffect, useRef, useState, type ChangeEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../../store/authStore'
import { useResumeStore } from '../../store/resumeStore'
import { getResumeImporter, resumeImporters, type ResumeImportType } from '../../utils/importers'
import { LogoMark } from '../branding/LogoMark'
import { AiConfigModal } from '../settings/AiConfigModal'

export function Header() {
  const navigate = useNavigate()
  const { isAuthenticated } = useAuthStore()
  const { importResume } = useResumeStore()
  const isDesktop = import.meta.env.VITE_DESKTOP === 'true'
  const [importMenuOpen, setImportMenuOpen] = useState(false)
  const [importingType, setImportingType] = useState<ResumeImportType | null>(null)
  const [importError, setImportError] = useState('')
  const [shuttingDown, setShuttingDown] = useState(false)
  const [settingsOpen, setSettingsOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement | null>(null)

  useEffect(() => {
    if (!importMenuOpen) {
      return
    }

    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node
      if (menuRef.current && !menuRef.current.contains(target)) {
        setImportMenuOpen(false)
      }
    }

    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [importMenuOpen])

  const handleImportChange = (type: ResumeImportType) => async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    event.target.value = ''
    if (!file) {
      return
    }

    const importer = getResumeImporter(type)
    if (!importer?.enabled || !importer.parse) {
      setImportError('当前导入方式暂不可用')
      return
    }

    setImportError('')
    setImportMenuOpen(false)
    setImportingType(type)
    try {
      const payload = await importer.parse(file)
      const resume = await importResume(payload)
      navigate(`/editor/${resume.id}`)
    } catch (error: unknown) {
      setImportError(error instanceof Error ? error.message : '导入失败，请稍后再试')
    } finally {
      setImportingType(null)
    }
  }

  const handleDesktopShutdown = async () => {
    if (!isDesktop || shuttingDown) {
      return
    }

    setShuttingDown(true)
    try {
      await fetch('/desktop/shutdown', {
        method: 'POST',
        keepalive: true,
      })
    } catch {
      // Ignore network errors while the local app is shutting down.
    } finally {
      window.setTimeout(() => {
        window.close()
        window.location.replace('about:blank')
      }, 150)
    }
  }

  return (
    <header className="sticky top-0 z-10 border-b border-gray-200 bg-white">
      <div className="w-full px-4 sm:px-6 lg:px-8">
        <div className="flex min-h-16 items-center justify-between gap-4 py-3">
          <Link to="/dashboard" className="flex items-center gap-3">
            <LogoMark className="h-9 w-9" />
            <span className="text-xl font-bold text-gray-900">ResumeCraft</span>
          </Link>

          {isAuthenticated && (
            <div className="flex items-center gap-4">
              <Link
                to="/dashboard"
                className="text-sm font-medium text-gray-600 transition-colors hover:text-primary-700"
              >
                我的简历
              </Link>

              <div className="relative" ref={menuRef}>
                <button
                  type="button"
                  onClick={() => setImportMenuOpen((open) => !open)}
                  disabled={!!importingType}
                  className="inline-flex items-center gap-2 rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 transition-colors hover:border-primary-200 hover:text-primary-700 disabled:cursor-not-allowed disabled:opacity-60"
                >
                  <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 16V6m0 0l-4 4m4-4l4 4M5 20h14" />
                  </svg>
                  {importingType ? `导入${getResumeImporter(importingType)?.label ?? ''}中...` : '导入'}
                </button>

                {importMenuOpen && (
                  <div className="absolute right-0 top-full z-20 mt-2 w-64 rounded-xl border border-gray-200 bg-white p-2 shadow-lg">
                    {resumeImporters.map((importer) => (
                      importer.enabled ? (
                        <label
                          key={importer.type}
                          className="relative block cursor-pointer rounded-lg px-3 py-2 text-left transition-colors hover:bg-gray-50"
                        >
                          <input
                            type="file"
                            accept={importer.accept}
                            className="absolute inset-0 h-full w-full cursor-pointer opacity-0"
                            onChange={handleImportChange(importer.type)}
                          />
                          <span className="block text-sm font-medium text-gray-700">{importer.label}</span>
                          <span className="mt-1 block text-xs text-gray-400">{importer.description}</span>
                        </label>
                      ) : null
                    ))}
                  </div>
                )}
              </div>

              {importError && <span className="max-w-xs text-sm text-red-500">{importError}</span>}
              <span className="rounded-full border border-gray-200 px-3 py-1 text-xs text-gray-600">
                本地模式
              </span>
              {isDesktop && (
                <>
                  <button
                    type="button"
                    onClick={() => setSettingsOpen(true)}
                    className="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 transition-colors hover:border-primary-200 hover:text-primary-700"
                    title="AI 配置"
                  >
                    <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.066 2.573c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.573 1.066c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.066-2.573c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                    </svg>
                  </button>
                  <button
                    type="button"
                    onClick={() => void handleDesktopShutdown()}
                    disabled={shuttingDown}
                    className="rounded-lg border border-gray-200 px-3 py-2 text-sm text-gray-600 transition-colors hover:border-gray-300 hover:text-gray-900 disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {shuttingDown ? '退出中...' : '退出应用'}
                  </button>
                </>
              )}
            </div>
          )}
        </div>
      </div>

      <AiConfigModal open={settingsOpen} onClose={() => setSettingsOpen(false)} />
    </header>
  )
}
