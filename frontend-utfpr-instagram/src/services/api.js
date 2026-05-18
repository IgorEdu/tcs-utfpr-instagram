// Configuração central da API usando o Fetch nativo (Sem depender do Axios)
const getBaseURL = () => {
  const ip = localStorage.getItem('api_ip')
  const port = localStorage.getItem('api_port')
  if (ip && port) {
    return `http://${ip}:${port}`
  }
  return import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
}

const getAuthHeaders = () => {
  const useFake = sessionStorage.getItem('use_fake_token') === 'true'
  const fakeToken = sessionStorage.getItem('fake_token')
  const realToken = sessionStorage.getItem('instagram_token')
  
  if (useFake && fakeToken !== null) {
    return { 'Authorization': `Bearer ${fakeToken}` }
  }
  
  return realToken ? { 'Authorization': `Bearer ${realToken}` } : {}
}

const fetchWithLogging = async (endpoint, options) => {
  const url = `${getBaseURL()}${endpoint}`
  const startTime = performance.now()
  
  console.log(`[⬆️ REQ] ${options.method} ${url}`, options.body ? JSON.parse(options.body) : '')

  try {
    const response = await fetch(url, options)
    const endTime = performance.now()
    const duration = Math.round(endTime - startTime)
    
    // Tentativa de ler a resposta (já que fetch não lê por padrão no log)
    // Clonamos para não consumir o body do response original
    const clone = response.clone()
    let responseData = null;
    try {
      responseData = await clone.json()
    } catch(e) { /* not json */ }

    if (response.ok) {
      console.log(`[⬇️ RES] ${options.method} ${url} | ${response.status} OK | ${duration}ms`, responseData)
    } else {
      console.warn(`[⚠️ RES] ${options.method} ${url} | ${response.status} ERRO | ${duration}ms`, responseData)
    }
    
    return response
  } catch (error) {
    const duration = Math.round(performance.now() - startTime)
    console.error(`[❌ FAIL] ${options.method} ${url} | Falha na Rede | ${duration}ms`, error)
    throw error
  }
}

const api = {
  async post(endpoint, data) {
    return fetchWithLogging(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeaders()
      },
      body: JSON.stringify(data)
    })
  },

  async get(endpoint) {
    return fetchWithLogging(endpoint, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeaders()
      }
    })
  },

  async patch(endpoint, data) {
    return fetchWithLogging(endpoint, {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
        ...getAuthHeaders()
      },
      body: JSON.stringify(data)
    })
  }
}

export default api
