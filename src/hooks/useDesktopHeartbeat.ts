import { useEffect } from 'react'

const HEARTBEAT_INTERVAL_MS = 10_000

export function useDesktopHeartbeat() {
  useEffect(() => {
    if (import.meta.env.VITE_DESKTOP !== 'true') {
      return
    }

    const id = setInterval(() => {
      fetch('/desktop/heartbeat').catch(() => {})
    }, HEARTBEAT_INTERVAL_MS)

    return () => clearInterval(id)
  }, [])
}
