import { useEffect } from 'react'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { useDesktopHeartbeat } from './hooks/useDesktopHeartbeat'
import DashboardPage from './pages/DashboardPage'
import EditorPage from './pages/EditorPage'
import FieldOptimizePage from './pages/FieldOptimizePage'
import ChromePreviewPage from './pages/ChromePreviewPage'
import { useAuthStore } from './store/authStore'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, initialized } = useAuthStore()

  if (!initialized) {
    return <div className="min-h-screen flex items-center justify-center text-sm text-gray-500">加载中...</div>
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center px-4 text-sm text-red-600">
        本地自动登录失败，请确认后端已启动并存在 test@example.com / Test123456。
      </div>
    )
  }

  return <>{children}</>
}

function App() {
  useDesktopHeartbeat()
  const ensureLocalSession = useAuthStore((state) => state.ensureLocalSession)

  useEffect(() => {
    void ensureLocalSession()
  }, [ensureLocalSession])

  return (
    <BrowserRouter>
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
    </BrowserRouter>
  )
}

export default App
