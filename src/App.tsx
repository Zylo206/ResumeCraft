import { Suspense, lazy, useEffect, useRef } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { useDesktopHeartbeat } from './hooks/useDesktopHeartbeat'
import { useAuthStore } from './store/authStore'

const DashboardPage = lazy(() => import('./pages/DashboardPage'))
const EditorPage = lazy(() => import('./pages/EditorPage'))
const FieldOptimizePage = lazy(() => import('./pages/FieldOptimizePage'))
const ChromePreviewPage = lazy(() => import('./pages/ChromePreviewPage'))

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, initialized } = useAuthStore()

  if (!initialized) {
    return <div className="min-h-screen flex items-center justify-center text-sm text-gray-500">加载中...</div>
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center px-4 text-sm text-red-600">
        本地自动登录失败，请确认后端已启动。
      </div>
    )
  }

  return <>{children}</>
}

function App() {
  useDesktopHeartbeat()
  const ensureLocalSession = useAuthStore((state) => state.ensureLocalSession)
  const lastSessionCheckRef = useRef(0)

  useEffect(() => {
    void ensureLocalSession()
  }, [ensureLocalSession])

  useEffect(() => {
    if (import.meta.env.VITE_DESKTOP !== 'true') {
      return
    }

    const SESSION_CHECK_DEBOUNCE_MS = 3_000

    const restoreSession = () => {
      if (document.hidden) {
        return
      }
      const now = Date.now()
      if (now - lastSessionCheckRef.current < SESSION_CHECK_DEBOUNCE_MS) {
        return
      }
      lastSessionCheckRef.current = now
      void ensureLocalSession()
    }

    document.addEventListener('visibilitychange', restoreSession)
    window.addEventListener('focus', restoreSession)

    return () => {
      document.removeEventListener('visibilitychange', restoreSession)
      window.removeEventListener('focus', restoreSession)
    }
  }, [ensureLocalSession])

  return (
    <BrowserRouter>
      <Suspense fallback={<div className="min-h-screen flex items-center justify-center text-sm text-gray-400">加载中...</div>}>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/login" element={<Navigate to="/dashboard" replace />} />
        <Route path="/register" element={<Navigate to="/dashboard" replace />} />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/editor/:id"
          element={
            <ProtectedRoute>
              <EditorPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/preview/:id"
          element={
            <ProtectedRoute>
              <ChromePreviewPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/editor/:id/modules/:moduleId/field-optimize"
          element={
            <ProtectedRoute>
              <FieldOptimizePage />
            </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
      </Suspense>
    </BrowserRouter>
  )
}

export default App
