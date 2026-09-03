import { useEffect, useRef, useState } from 'react'
import { loadKakaoMaps } from '@/lib/kakaoMap'

const DEFAULT_CENTER = { lat: 37.5665, lng: 126.978 }

export function KakaoMapPicker({ latitude, longitude, onChange }) {
  const containerRef = useRef(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    loadKakaoMaps()
      .then((kakao) => {
        if (cancelled || !containerRef.current) {
          return
        }

        const initialLat = latitude ? Number(latitude) : DEFAULT_CENTER.lat
        const initialLng = longitude ? Number(longitude) : DEFAULT_CENTER.lng
        const center = new kakao.maps.LatLng(initialLat, initialLng)
        const map = new kakao.maps.Map(containerRef.current, { center, level: 4 })
        const marker = new kakao.maps.Marker({ map, position: center });

        kakao.maps.event.addListener(map, 'click', (mouseEvent) => {
          const latLng = mouseEvent.latLng
          marker.setPosition(latLng)
          onChange(latLng.getLat(), latLng.getLng())
        })
      })
      .catch((err) => setError(err.message))

    return () => {
      cancelled = true
    }
    // 최초 마운트 시에만 지도를 생성하고, 이후 좌표 변경은 클릭 이벤트로만 반영합니다.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  if (error) {
    return (
      <div className="flex h-48 w-full flex-col items-center justify-center gap-1 rounded-lg bg-muted p-4 text-center text-sm text-muted-foreground">
        <p>{error}</p>
        <p className="text-xs">잠시 후 다시 시도해주세요.</p>
      </div>
    )
  }

  return <div ref={containerRef} className="h-48 w-full rounded-lg" />
}
