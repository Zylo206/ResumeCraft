import { useEffect, useRef } from 'react'

const HEARTBEAT_INTERVAL_MS = 10_000
const HEARTBEAT_DEBOUNCE_MS = 1_000

export function useDesktopHeartbeat() {
  const lastHeartbeatRef = useRef(0)

  useEffect(() => {
    if (import.meta.env.VITE_DESKTOP !== 'true') {
      return
    }

    const sendHeartbeat = () => {
      const now = Date.now()
      if (now - lastHeartbeatRef.current < HEARTBEAT_DEBOUNCE_MS) {
        return
      }
      lastHeartbeatRef.current = now
      fetch('/desktop/heartbeat', { cache: 'no-store', keepalive: true }).catch(() => {})
    }

    sendHeartbeat()

    const id = setInterval(sendHeartbeat, HEARTBEAT_INTERVAL_MS)

    const onVisibilityChange = () => {
      if (!document.hidden) {
        sendHeartbeat()
      }
    }

    const onFocus = () => {
      sendHeartbeat()
    }

    document.addEventListener('visibilitychange', onVisibilityChange)
    window.addEventListener('focus', onFocus)

    return () => {
      clearInterval(id)
      document.removeEventListener('visibilitychange', onVisibilityChange)
      window.removeEventListener('focus', onFocus)
    }
  }, [])
}
