import { useEffect, useRef, useState } from 'react'
import { loadKakaoMaps } from '@/lib/kakaoMap'

export function KakaoMap({ latitude, longitude, level = 3 }) {
  const containerRef = useRef(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    loadKakaoMaps()
      .then((kakao) => {
        if (cancelled || !containerRef.current) {
          return
        }
        const center = new kakao.maps.LatLng(latitude, longitude)
        const map = new kakao.maps.Map(containerRef.current, { center, level })
        new kakao.maps.Marker({ map, position: center })
      })
      .catch((err) => setError(err.message))

    return () => {
      cancelled = true
    }
  }, [latitude, longitude, level])

  if (error) {
    return (
      <div className="flex h-48 w-full items-center justify-center rounded-lg bg-muted p-4 text-center text-sm text-muted-foreground">
        {error}
      </div>
    )
  }

  return <div ref={containerRef} className="h-48 w-full rounded-lg" />
}
