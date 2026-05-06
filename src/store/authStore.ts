import { create } from 'zustand'
import { authApi, type TokenData } from '../api/auth'

interface AuthState {
  user: TokenData['userInfo'] | null
  isAuthenticated: boolean
  initialized: boolean
  restoring: boolean
  login: (email: string, password: string) => Promise<void>
  ensureLocalSession: () => Promise<void>
  register: (email: string, password: string, verificationCode: string) => Promise<void>
  logout: () => Promise<void>
  sendCode: (email: string) => Promise<void>
  restoreSession: () => Promise<void>
}

function hasStoredSession() {
  if (typeof window === 'undefined') {
    return false
  }
  return Boolean(window.localStorage.getItem('accessToken'))
}

const LOCAL_DEV_EMAIL = import.meta.env.VITE_LOCAL_DEV_EMAIL || 'test@example.com'
const LOCAL_DEV_PASSWORD = import.meta.env.VITE_LOCAL_DEV_PASSWORD || 'Test123456'
const IS_DESKTOP = import.meta.env.VITE_DESKTOP === 'true'
const DESKTOP_READY_RETRY_COUNT = 20
const DESKTOP_LOGIN_RETRY_COUNT = 12
const DESKTOP_RETRY_DELAY_MS = 1000

function sleep(ms: number) {
  return new Promise((resolve) => window.setTimeout(resolve, ms))
}

async function waitForDesktopBackendReady() {
  if (!IS_DESKTOP) {
    return
  }

  for (let attempt = 0; attempt < DESKTOP_READY_RETRY_COUNT; attempt += 1) {
    try {
      const response = await fetch('/health', { cache: 'no-store' })
      if (response.ok) {
        return
      }
    } catch {
      // Ignore startup race and retry.
    }

    await sleep(DESKTOP_RETRY_DELAY_MS)
  }
}

async function loginWithRetry() {
  const maxAttempts = IS_DESKTOP ? DESKTOP_LOGIN_RETRY_COUNT : 1
  let lastError: unknown = null

  for (let attempt = 0; attempt < maxAttempts; attempt += 1) {
    try {
      return await authApi.login({
        email: LOCAL_DEV_EMAIL,
        password: LOCAL_DEV_PASSWORD,
      })
    } catch (error) {
      lastError = error
      if (attempt < maxAttempts - 1) {
        await sleep(DESKTOP_RETRY_DELAY_MS)
      }
    }
  }

  throw lastError instanceof Error ? lastError : new Error('Local desktop login failed')
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  isAuthenticated: hasStoredSession(),
  initialized: false,
  restoring: false,

  login: async (email, password) => {
    const { data: res } = await authApi.login({ email, password })
    const tokenData = res.data
    localStorage.setItem('accessToken', tokenData.accessToken)
    localStorage.setItem('refreshToken', tokenData.refreshToken)
    set({ user: tokenData.userInfo, isAuthenticated: true, initialized: true })
  },

  ensureLocalSession: async () => {
    const token = localStorage.getItem('accessToken')

    set({ restoring: true })
    await waitForDesktopBackendReady()

    if (token) {
      try {
        const { data: res } = await authApi.me()
        set({
          user: res.data,
          isAuthenticated: true,
          initialized: true,
          restoring: false,
        })
        return
      } catch {
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
      }
    }

    try {
      const { data: res } = await loginWithRetry()
      const tokenData = res.data
      localStorage.setItem('accessToken', tokenData.accessToken)
      localStorage.setItem('refreshToken', tokenData.refreshToken)
      set({
        user: tokenData.userInfo,
        isAuthenticated: true,
        initialized: true,
        restoring: false,
      })
    } catch {
      set({
        user: null,
        isAuthenticated: false,
        initialized: true,
        restoring: false,
      })
    }
  },

  register: async (email, password, verificationCode) => {
    const { data: res } = await authApi.register({ email, password, verificationCode })
    const tokenData = res.data
    localStorage.setItem('accessToken', tokenData.accessToken)
    localStorage.setItem('refreshToken', tokenData.refreshToken)
    set({ user: tokenData.userInfo, isAuthenticated: true, initialized: true })
  },

  logout: async () => {
    try {
      await authApi.logout()
    } catch {
      // ignore
    }
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    set({ user: null, isAuthenticated: false, initialized: true, restoring: false })
  },

  sendCode: async (email) => {
    await authApi.sendCode(email)
  },

  restoreSession: async () => {
    const token = localStorage.getItem('accessToken')

    if (!token) {
      set({ user: null, isAuthenticated: false, initialized: true, restoring: false })
      return
    }

    set({ restoring: true })
    try {
      const { data: res } = await authApi.me()
      set({
        user: res.data,
        isAuthenticated: true,
        initialized: true,
        restoring: false,
      })
    } catch {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      set({
        user: null,
        isAuthenticated: false,
        initialized: true,
        restoring: false,
      })
    }
  },
}))
