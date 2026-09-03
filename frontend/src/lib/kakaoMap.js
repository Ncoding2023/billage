let loadPromise = null

export function loadKakaoMaps() {
  if (loadPromise) {
    return loadPromise
  }

  const appKey = import.meta.env.VITE_KAKAO_MAP_KEY
  if (!appKey) {
    return Promise.reject(
      new Error('카카오맵 API 키가 설정되지 않았습니다 (VITE_KAKAO_MAP_KEY).'),
    )
  }

  loadPromise = new Promise((resolve, reject) => {
    if (window.kakao?.maps) {
      resolve(window.kakao)
      return
    }

    const script = document.createElement('script')
    script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=${appKey}&autoload=false&libraries=services`
    script.onload = () => window.kakao.maps.load(() => resolve(window.kakao))
    script.onerror = () => reject(new Error('카카오맵 SDK를 불러오지 못했습니다.'))
    document.head.appendChild(script)
  })

  return loadPromise
}
