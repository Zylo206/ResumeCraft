interface LogoMarkProps {
  className?: string
}

export function LogoMark({ className = 'h-10 w-10' }: LogoMarkProps) {
  return (
    <img
      src="/logo.png"
      alt="ResumeCraft"
      className={`rounded-full object-contain ${className}`}
    />
  )
}
