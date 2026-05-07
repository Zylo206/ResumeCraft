import { useEffect, useState } from 'react'
import { aiConfigApi, type AiConfig } from '../../api/resume'

interface Props {
  open: boolean
  onClose: () => void
}

export function AiConfigModal({ open, onClose }: Props) {
  const [apiKey, setApiKey] = useState('')
  const [baseUrl, setBaseUrl] = useState('')
  const [model, setModel] = useState('')
  const [analysisModel, setAnalysisModel] = useState('')
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)

  useEffect(() => {
    if (!open) return
    setError('')
    setSuccess(false)
    setLoading(true)
    aiConfigApi.get()
      .then(({ data: res }) => {
        const cfg = res.data
        setBaseUrl(cfg.baseUrl || '')
        setModel(cfg.model || '')
        setAnalysisModel(cfg.analysisModel || '')
        // apiKey is masked, don't fill it
        setApiKey('')
      })
      .catch(() => setError('加载配置失败'))
      .finally(() => setLoading(false))
  }, [open])

  const handleSave = async () => {
    setSaving(true)
    setError('')
    setSuccess(false)
    try {
      const updates: Partial<AiConfig> = {}
      if (apiKey.trim()) updates.apiKey = apiKey.trim()
      if (baseUrl.trim()) updates.baseUrl = baseUrl.trim()
      if (model.trim()) updates.model = model.trim()
      if (analysisModel.trim()) updates.analysisModel = analysisModel.trim()
      await aiConfigApi.update(updates)
      setSuccess(true)
      setApiKey('')
    } catch {
      setError('保存失败')
    } finally {
      setSaving(false)
    }
  }

  if (!open) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4">
      <div className="w-full max-w-lg rounded-2xl bg-white p-6 shadow-xl">
        <div className="mb-5">
          <h2 className="text-lg font-semibold text-gray-900">AI 配置</h2>
          <p className="mt-1 text-sm text-gray-500">
            配置 OpenAI 兼容格式的 API，用于简历 AI 优化和分析功能。
          </p>
        </div>

        {loading ? (
          <div className="py-8 text-center text-sm text-gray-400">加载中...</div>
        ) : (
          <div className="space-y-4">
            <div>
              <label className="mb-1.5 block text-sm font-medium text-gray-700">API Key</label>
              <input
                type="password"
                value={apiKey}
                onChange={(e) => setApiKey(e.target.value)}
                placeholder="留空则不修改当前值"
                className="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div>
              <label className="mb-1.5 block text-sm font-medium text-gray-700">Base URL</label>
              <input
                type="text"
                value={baseUrl}
                onChange={(e) => setBaseUrl(e.target.value)}
                placeholder="https://api.openai.com/v1"
                className="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
              />
              <p className="mt-1 text-xs text-gray-400">支持 OpenAI / DeepSeek / 智谱 等兼容接口</p>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-700">优化模型</label>
                <input
                  type="text"
                  value={model}
                  onChange={(e) => setModel(e.target.value)}
                  placeholder="gpt-4o"
                  className="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                />
              </div>
              <div>
                <label className="mb-1.5 block text-sm font-medium text-gray-700">分析模型</label>
                <input
                  type="text"
                  value={analysisModel}
                  onChange={(e) => setAnalysisModel(e.target.value)}
                  placeholder="gpt-4o"
                  className="w-full rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none focus:border-primary-500 focus:ring-2 focus:ring-primary-500"
                />
              </div>
            </div>
          </div>
        )}

        {error && <p className="mt-3 text-sm text-red-600">{error}</p>}
        {success && <p className="mt-3 text-sm text-green-600">保存成功</p>}

        <div className="mt-6 flex justify-end gap-3">
          <button
            type="button"
            onClick={onClose}
            disabled={saving}
            className="rounded-lg px-4 py-2 text-sm text-gray-600 transition-colors hover:bg-gray-100"
          >
            关闭
          </button>
          <button
            type="button"
            onClick={() => void handleSave()}
            disabled={saving || loading}
            className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-700 disabled:opacity-50"
          >
            {saving ? '保存中...' : '保存'}
          </button>
        </div>
      </div>
    </div>
  )
}
