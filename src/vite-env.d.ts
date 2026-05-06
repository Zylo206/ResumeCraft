/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string
  readonly VITE_API_PROXY_TARGET?: string
  readonly VITE_AI_API_KEY?: string
  readonly VITE_AI_BASE_URL?: string
  readonly VITE_AI_MODEL?: string
  readonly VITE_LOCAL_DEV_EMAIL?: string
  readonly VITE_LOCAL_DEV_PASSWORD?: string
  readonly VITE_DESKTOP?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
