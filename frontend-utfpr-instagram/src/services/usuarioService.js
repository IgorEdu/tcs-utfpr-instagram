import api from './api'

export const usuarioService = {
  /**
   * Envia payload para criar nova conta de usuário.
   * Contrato definido em `docs/api-specs.json` (CadastroRequest)
   */
  async cadastrar(usuarioData) {
    // Retorna a promessa com a resposta inteira da rede (Headers, Status Code, Date), agnóstico de customizações do JSON
    const response = await api.post('/usuarios', usuarioData)
    return response
  },

  /**
   * Envia payload para autenticação de usuário existente.
   */
  async login(credenciais) {
    const response = await api.post('/usuarios/login', credenciais)
    return response
  },

  /**
   * Obtém os dados de um usuário pelo seu ID
   */
  async obterPorId(id) {
    const response = await api.get(`/usuarios/${id}`)
    return response
  },

  /**
   * Atualiza os dados do usuário via PATCH
   */
  async atualizar(id, dados) {
    const response = await api.patch(`/usuarios/${id}`, dados)
    return response
  },

  /**
   * Faz o logout no servidor invalidando o token
   */
  async logout() {
    const response = await api.post('/usuarios/logout')
    return response
  }
}
