// Configuração central da API usando o Fetch nativo (Sem depender do Axios)
const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

const getAuthHeaders = () => {
  const token = sessionStorage.getItem('instagram_token')
  return token ? { 'Authorization': `Bearer ${token}` } : {}
}

const fetchWithLogging = async (endpoint, options) => {
  const url = `${baseURL}${endpoint}`
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
