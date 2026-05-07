import { Component, type ErrorInfo, type ReactNode } from 'react'

interface Props {
  children: ReactNode
}

interface State {
  hasError: boolean
  error: Error | null
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    console.error('ErrorBoundary caught:', error, info)
  }

  handleRetry = () => {
    this.setState({ hasError: false, error: null })
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-screen flex-col items-center justify-center gap-4 p-8 text-center">
          <div className="rounded-2xl border border-red-200 bg-red-50 px-6 py-5">
            <h2 className="text-lg font-semibold text-red-800">页面出错了</h2>
            <p className="mt-2 text-sm text-red-600">{this.state.error?.message || '未知错误'}</p>
          </div>
          <button
            onClick={this.handleRetry}
            className="rounded-lg bg-primary-600 px-5 py-2.5 text-sm font-medium text-white hover:bg-primary-700 transition-colors"
          >
            重试
          </button>
        </div>
      )
    }

    return this.props.children
  }
}
