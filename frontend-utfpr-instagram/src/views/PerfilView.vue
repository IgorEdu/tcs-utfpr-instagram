<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { usuarioService } from '@/services/usuarioService'
import { postService } from '@/services/postService'
import PostCard from '@/components/PostCard.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const perfilData = ref(null)
const isLoading = ref(true)
const errorMessage = ref(null)
const currentProfileId = ref(null)

const editingField = ref(null)
const editForm = ref({
  nome: '',
  usuario: '',
  email: '',
  biografia: ''
})
const isSaving = ref(false)

onMounted(async () => {
  currentProfileId.value = route.params.id || authStore.user?.id

  if (!currentProfileId.value) {
    isLoading.value = false
    errorMessage.value = 'Usuário não especificado ou não autenticado'
    return
  }

  await loadProfile()
  await loadPosts()
})

const loadProfile = async () => {
  isLoading.value = true
  errorMessage.value = null
  try {
    const resp = await usuarioService.obterPorId(currentProfileId.value)
    if (resp.ok) {
      const data = await resp.json()
      perfilData.value = data.dados
    } else {
      errorMessage.value = 'Falha ao carregar informações do perfil.'
    }
  } catch (error) {
    errorMessage.value = 'Erro de comunicação com o servidor.'
  } finally {
    isLoading.value = false
  }
}

const startEditing = (field) => {
  editingField.value = field
  editForm.value[field] = perfilData.value[field] || ''
  errorMessage.value = null
}

const cancelEditing = () => {
  editingField.value = null
  errorMessage.value = null
}

const saveEdit = async (field) => {
  if (!editForm.value[field] && field !== 'email') {
    errorMessage.value = 'Este campo não pode ser vazio.'
    return
  }
  
  isSaving.value = true
  errorMessage.value = null

  try {
    const payload = { [field]: editForm.value[field] }
    const resp = await usuarioService.atualizar(currentProfileId.value, payload)
    
    if (resp.ok) {
      perfilData.value[field] = editForm.value[field]
      
      // Atualiza a store global se for um dado de exibição primária do próprio usuário
      if (currentProfileId.value == authStore.user?.id && (field === 'nome' || field === 'usuario')) {
        authStore.user[field] = editForm.value[field]
        sessionStorage.setItem('instagram_user', JSON.stringify(authStore.user))
      }
      
      editingField.value = null
    } else {
      const errData = await resp.json().catch(() => null)
      errorMessage.value = errData?.mensagem || 'Falha ao atualizar dado.'
    }
  } catch (error) {
    errorMessage.value = 'Erro de comunicação.'
  } finally {
    isSaving.value = false
  }
}

// Lógica de alteração de senha
const showPasswordForm = ref(false)
const passwordForm = ref({
  senha: '',
  confirmarSenha: ''
})
const passwordError = ref(null)
const passwordSuccess = ref(false)
const isSavingPassword = ref(false)

const togglePasswordForm = () => {
  showPasswordForm.value = !showPasswordForm.value
  passwordForm.value.senha = ''
  passwordForm.value.confirmarSenha = ''
  passwordError.value = null
  passwordSuccess.value = false
}

const handleLogout = async () => {
  try {
    await usuarioService.logout()
  } catch (e) {
    console.error('Erro ao fazer logout', e)
  } finally {
    authStore.logout()
    router.push('/login')
  }
}

const savePassword = async () => {
  passwordError.value = null
  passwordSuccess.value = false
  
  if (!passwordForm.value.senha || passwordForm.value.senha.length < 8 || passwordForm.value.senha.length > 24) {
    passwordError.value = 'A senha deve ter entre 8 e 24 caracteres.'
    return
  }
  
  if (!/^[a-zA-Z0-9]+$/.test(passwordForm.value.senha)) {
    passwordError.value = 'A senha deve conter apenas letras e números.'
    return
  }
  
  if (passwordForm.value.senha !== passwordForm.value.confirmarSenha) {
    passwordError.value = 'As senhas não coincidem.'
    return
  }
  
  isSavingPassword.value = true
  
  try {
    const payload = { senha: passwordForm.value.senha }
    const resp = await usuarioService.atualizar(currentProfileId.value, payload)
    
    if (resp.ok) {
      passwordSuccess.value = true
      setTimeout(() => {
        showPasswordForm.value = false
      }, 2500)
    } else {
      const errData = await resp.json().catch(() => null)
      passwordError.value = errData?.mensagem || 'Falha ao atualizar senha.'
    }
  } catch (error) {
    passwordError.value = 'Erro de comunicação.'
  } finally {
    isSavingPassword.value = false
  }
}

// Lógica de exclusão de conta
const isDeletingAccount = ref(false)

const handleDeleteAccount = async () => {
  if (!window.confirm("Tem certeza que deseja excluir esta conta? Esta ação é irreversível e todos os dados serão apagados permanentemente.")) {
    return
  }

  isDeletingAccount.value = true
  errorMessage.value = null

  try {
    const resp = await usuarioService.excluir(currentProfileId.value)
    if (resp.ok) {
      if (currentProfileId.value == authStore.user?.id) {
        // Exclusão bem sucedida, limpar auth e ir para login
        authStore.logout()
        router.push('/login')
      } else {
        // Admin excluindo outra conta
        alert("Conta excluída com sucesso.")
        router.push('/admin')
      }
    } else {
      const errData = await resp.json().catch(() => null)
      errorMessage.value = errData?.mensagem || 'Falha ao excluir a conta.'
    }
  } catch (error) {
    errorMessage.value = 'Erro de comunicação ao tentar excluir a conta.'
  } finally {
    isDeletingAccount.value = false
  }
}

// Logica de Postagens
const posts = ref([])
const isPostsLoading = ref(false)
const postsError = ref(null)

const newPost = ref({ imagem: '', legenda: '' })
const isCreatingPost = ref(false)
const showCreatePostModal = ref(false)

const loadPosts = async () => {
  isPostsLoading.value = true
  postsError.value = null
  try {
    const resp = await postService.listar(currentProfileId.value)
    if (resp.ok) {
      const data = await resp.json()
      posts.value = data.posts || (data.dados && data.dados.posts) || []
    } else {
      postsError.value = 'Falha ao carregar posts.'
    }
  } catch (error) {
    postsError.value = 'Erro de rede ao carregar posts.'
  } finally {
    isPostsLoading.value = false
  }
}

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (file.size > 5 * 1024 * 1024) {
    alert("A imagem não pode ter mais de 5MB")
    return
  }
  const reader = new FileReader()
  reader.onload = (event) => {
    newPost.value.imagem = event.target.result
  }
  reader.readAsDataURL(file)
}

const handleCreatePost = async () => {
  if (!newPost.value.imagem) {
    alert("Selecione uma imagem.")
    return
  }
  if (newPost.value.legenda.length < 5 || newPost.value.legenda.length > 200) {
    alert("A legenda deve ter entre 5 e 200 caracteres.")
    return
  }

  isCreatingPost.value = true
  try {
    const resp = await postService.criar(currentProfileId.value, newPost.value)
    if (resp.ok) {
      showCreatePostModal.value = false
      newPost.value = { imagem: '', legenda: '' }
      await loadPosts()
    } else {
      const err = await resp.json().catch(() => null)
      alert(err?.mensagem || "Erro ao criar post.")
    }
  } catch (error) {
    alert("Falha de rede ao criar post.")
  } finally {
    isCreatingPost.value = false
  }
}

const onCurtir = async (postId) => {
  try {
    const resp = await postService.curtir(currentProfileId.value, postId)
    if (resp.ok) await loadPosts()
  } catch(e) {}
}

const onDeletar = async (postId) => {
  try {
    const resp = await postService.deletar(currentProfileId.value, postId)
    if (resp.ok) {
      posts.value = posts.value.filter(p => p.id !== postId)
    }
  } catch(e) {}
}

const onAtualizar = async (postId, novaLegenda) => {
  try {
    const resp = await postService.atualizar(currentProfileId.value, postId, { legenda: novaLegenda })
    if (resp.ok) {
      const postIndex = posts.value.findIndex(p => p.id === postId)
      if (postIndex !== -1) posts.value[postIndex].legenda = novaLegenda
    }
  } catch(e) {}
}
</script>

<template>
  <div class="perfil-container">
    <header class="top-nav">
      <div class="nav-content">
        <RouterLink to="/home" class="logo">Instagram</RouterLink>
        <nav class="actions">
          <RouterLink to="/configuracoes" class="btn-icon" title="Configurações">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="width: 22px; height: 22px;"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
          </RouterLink>
          <button @click="handleLogout" class="btn-logout">Sair</button>
        </nav>
      </div>
    </header>

    <main class="perfil-content">
      <div class="profile-header">
        <div class="avatar-placeholder"></div>
        <div class="profile-info">
          <div v-if="isLoading" class="loader">Carregando...</div>
          <div v-else-if="errorMessage && !editingField" class="error-msg">{{ errorMessage }}</div>
          
          <template v-else-if="perfilData">
            <!-- Nome -->
            <div class="field-row title-row">
              <div v-if="editingField === 'nome'" class="edit-mode">
                <input v-model="editForm.nome" class="edit-input" placeholder="Nome Completo" />
                <div class="edit-actions">
                  <button @click="saveEdit('nome')" :disabled="isSaving" class="btn-save">Salvar</button>
                  <button @click="cancelEditing" class="btn-cancel" :disabled="isSaving">Cancelar</button>
                </div>
              </div>
              <div v-else class="view-mode">
                <h2>{{ perfilData.nome }}</h2>
                <button class="btn-icon" @click="startEditing('nome')" title="Editar Nome">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-pencil"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
              </div>
            </div>

            <!-- Usuário -->
            <div class="field-row">
              <div v-if="editingField === 'usuario'" class="edit-mode">
                <input v-model="editForm.usuario" class="edit-input" placeholder="Nome de usuário" />
                <div class="edit-actions">
                  <button @click="saveEdit('usuario')" :disabled="isSaving" class="btn-save">Salvar</button>
                  <button @click="cancelEditing" class="btn-cancel" :disabled="isSaving">Cancelar</button>
                </div>
              </div>
              <div v-else class="view-mode">
                <p class="text-secondary">@{{ perfilData.usuario }} <span class="user-id">(ID: {{ perfilData.id }})</span></p>
                <button class="btn-icon" @click="startEditing('usuario')" title="Editar Usuário">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-pencil"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
              </div>
            </div>

            <!-- Email -->
            <div class="field-row">
              <div v-if="editingField === 'email'" class="edit-mode">
                <input v-model="editForm.email" type="email" class="edit-input" placeholder="Email" />
                <div class="edit-actions">
                  <button @click="saveEdit('email')" :disabled="isSaving" class="btn-save">Salvar</button>
                  <button @click="cancelEditing" class="btn-cancel" :disabled="isSaving">Cancelar</button>
                </div>
              </div>
              <div v-else class="view-mode">
                <p class="text-secondary email" v-if="perfilData.email">{{ perfilData.email }}</p>
                <p class="text-secondary placeholder" v-else>Nenhum email cadastrado</p>
                <button class="btn-icon" @click="startEditing('email')" title="Editar Email">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-pencil"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
              </div>
            </div>

            <!-- Biografia -->
            <div class="field-row">
              <div v-if="editingField === 'biografia'" class="edit-mode">
                <textarea v-model="editForm.biografia" class="edit-input" placeholder="Sua biografia..."></textarea>
                <div class="edit-actions">
                  <button @click="saveEdit('biografia')" :disabled="isSaving" class="btn-save">Salvar</button>
                  <button @click="cancelEditing" class="btn-cancel" :disabled="isSaving">Cancelar</button>
                </div>
              </div>
              <div v-else class="view-mode">
                <p class="text-secondary biografia" v-if="perfilData.biografia">{{ perfilData.biografia }}</p>
                <p class="text-secondary placeholder" v-else>Nenhuma biografia cadastrada</p>
                <button class="btn-icon" @click="startEditing('biografia')" title="Editar Biografia">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-pencil"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                </button>
              </div>
            </div>

            <div v-if="errorMessage && editingField" class="error-msg inline-error">{{ errorMessage }}</div>
            
            <!-- Alterar Senha -->
            <div class="field-row" style="margin-top: 1rem; border-top: 1px solid var(--border-color, #333); padding-top: 1.5rem;">
              <button v-if="!showPasswordForm" @click="togglePasswordForm" class="btn-cancel" style="width: max-content;">Alterar Senha</button>
              
              <div v-else class="password-form">
                <h3>Alterar Senha</h3>
                <div class="input-group">
                  <input v-model="passwordForm.senha" type="password" class="edit-input auth-input" placeholder="Nova Senha" />
                </div>
                <div class="input-group">
                  <input v-model="passwordForm.confirmarSenha" type="password" class="edit-input auth-input" placeholder="Confirmar Nova Senha" />
                </div>
                
                <div v-if="passwordError" class="error-msg inline-error">{{ passwordError }}</div>
                <div v-if="passwordSuccess" class="success-msg">Senha alterada com sucesso!</div>
                
                <div class="edit-actions" style="margin-top: 0.5rem;">
                  <button @click="savePassword" :disabled="isSavingPassword" class="btn-save">Salvar Senha</button>
                  <button @click="togglePasswordForm" class="btn-cancel" :disabled="isSavingPassword">Cancelar</button>
                </div>
              </div>
            </div>

            <!-- Zona de Perigo (Descadastrar) -->
            <div class="field-row danger-zone" v-if="currentProfileId == authStore.user?.id || authStore.user?.is_admin" style="margin-top: 2rem; border-top: 1px solid var(--color-error-bg, rgba(237, 73, 86, 0.3)); padding-top: 1.5rem;">
              <h3 style="color: var(--color-error, #ed4956); margin-top: 0; margin-bottom: 0.5rem; font-size: 1.2rem; font-weight: 500;">Zona de Perigo</h3>
              <p class="text-secondary" style="margin-bottom: 1rem; font-size: 0.95rem;">Ao excluir esta conta, todos os dados serão perdidos permanentemente.</p>
              <button @click="handleDeleteAccount" class="btn-delete-account" :disabled="isDeletingAccount" style="width: max-content;">
                {{ isDeletingAccount ? 'Excluindo...' : 'Excluir conta' }}
              </button>
            </div>
          </template>
        </div>
      </div>

      <!-- Feed Section -->
      <div class="posts-section">
        <div class="posts-header">
          <h3>Publicações</h3>
          <button v-if="currentProfileId == authStore.user?.id" @click="showCreatePostModal = !showCreatePostModal" class="btn-save">
            {{ showCreatePostModal ? 'Cancelar Post' : '+ Novo Post' }}
          </button>
        </div>

        <div v-if="showCreatePostModal" class="create-post-container">
          <div class="input-group" style="margin-bottom: 1rem;">
            <input type="file" accept="image/jpeg, image/png, image/jpg" @change="handleFileChange" style="color: white;"/>
          </div>
          <div v-if="newPost.imagem" class="preview-img-container">
            <img :src="newPost.imagem" alt="Preview" class="preview-img" />
          </div>
          <div class="input-group">
            <textarea v-model="newPost.legenda" class="edit-input" placeholder="Escreva uma legenda (5 a 200 caracteres)..." rows="3"></textarea>
          </div>
          <button @click="handleCreatePost" :disabled="isCreatingPost" class="btn-save" style="margin-top: 1rem;">
            {{ isCreatingPost ? 'Publicando...' : 'Publicar' }}
          </button>
        </div>

        <div v-if="isPostsLoading" class="loader" style="margin-top: 2rem;">Carregando publicações...</div>
        <div v-else-if="postsError" class="error-msg" style="margin-top: 2rem;">{{ postsError }}</div>
        <div v-else-if="posts.length === 0" class="no-posts">
          Nenhuma publicação ainda.
        </div>
        <div v-else class="posts-grid">
          <PostCard 
            v-for="post in posts" 
            :key="post.id" 
            :post="post" 
            :isOwner="currentProfileId == authStore.user?.id || authStore.user?.is_admin"
            @curtir="onCurtir"
            @deletar="onDeletar"
            @atualizar="onAtualizar"
          />
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.perfil-container {
  min-height: 100vh;
  background-color: var(--bg-primary, #000);
  color: var(--text-primary, #fff);
}

.top-nav {
  border-bottom: 1px solid var(--border-color, #333);
  background-color: var(--bg-secondary, #111);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-content {
  max-width: 935px;
  margin: 0 auto;
  padding: 0 1rem;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.btn-logout {
  background: transparent;
  color: var(--color-error, #ed4956);
  border: 1px solid var(--color-error, #ed4956);
  padding: 0.4rem 0.8rem;
  border-radius: var(--border-radius-base, 4px);
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.btn-logout:hover {
  background: var(--color-error-bg, rgba(237, 73, 86, 0.1));
}

.logo {
  font-family: 'Times New Roman', serif;
  font-size: 1.5rem;
  color: var(--text-primary, #fff);
  text-decoration: none;
  margin: 0;
}

.perfil-content {
  max-width: 935px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 3rem;
  margin-bottom: 2rem;
  padding-bottom: 2rem;
  border-bottom: 1px solid var(--border-color, #333);
}

.avatar-placeholder {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background-color: var(--bg-tertiary, #262626);
  border: 1px solid var(--border-color, #333);
  flex-shrink: 0;
}

.profile-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.field-row {
  display: flex;
  flex-direction: column;
}

.view-mode {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.view-mode h2 {
  font-size: 1.75rem;
  font-weight: 300;
  margin: 0;
}

.text-secondary {
  color: var(--text-secondary, #a8a8a8);
  font-size: 1.1rem;
  margin: 0;
}

.user-id {
  font-size: 0.9rem;
  opacity: 0.7;
}

.placeholder {
  font-style: italic;
  opacity: 0.6;
}

.btn-icon {
  background: none;
  border: none;
  color: var(--text-secondary, #a8a8a8);
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: color 0.2s, background-color 0.2s;
}

.btn-icon:hover {
  color: var(--text-primary, #fff);
  background-color: var(--bg-tertiary, #262626);
}

.icon-pencil {
  width: 18px;
  height: 18px;
}

/* Edit Mode */
.edit-mode {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: var(--bg-tertiary, #262626);
  padding: 0.5rem 1rem;
  border-radius: 8px;
}

.edit-input {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--text-primary, #fff);
  font-size: 1.1rem;
  outline: none;
  border-bottom: 1px solid var(--border-color, #333);
  padding: 0.25rem 0;
}

textarea.edit-input {
  resize: vertical;
  min-height: 60px;
  border: 1px solid var(--border-color, #333);
  padding: 0.5rem;
  border-radius: 4px;
}

.edit-input:focus {
  border-color: var(--color-brand, #0095f6);
}

.edit-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-save {
  background-color: var(--color-brand, #0095f6);
  color: #fff;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9rem;
}

.btn-save:hover:not(:disabled) {
  background-color: var(--color-brand-hover, #1877f2);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: transparent;
  color: var(--text-primary, #fff);
  border: 1px solid var(--border-color, #333);
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9rem;
}

.btn-cancel:hover:not(:disabled) {
  background-color: var(--bg-primary, #000);
}

.loader {
  color: var(--color-brand, #0095f6);
  font-size: 1.1rem;
  font-weight: 500;
  animation: pulse 1.5s infinite ease-in-out;
}

@keyframes pulse {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}

.error-msg {
  color: var(--color-error, #ed4956);
  background: var(--color-error-bg, rgba(237, 73, 86, 0.1));
  padding: 0.75rem;
  border-radius: var(--border-radius-base, 8px);
  font-size: 0.95rem;
  border: 1px solid var(--color-error, #ed4956);
}

.inline-error {
  margin-top: 0.5rem;
  padding: 0.5rem;
  font-size: 0.85rem;
}

/* Password Form */
.password-form {
  background: var(--bg-tertiary, #262626);
  padding: 1.5rem;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.password-form h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 500;
  color: var(--text-primary, #fff);
}

.auth-input {
  width: 100%;
}

.success-msg {
  color: #4ade80;
  background: rgba(74, 222, 128, 0.1);
  padding: 0.75rem;
  border-radius: var(--border-radius-base, 8px);
  font-size: 0.95rem;
  border: 1px solid #4ade80;
}

/* Danger Zone */
.btn-delete-account {
  background-color: transparent;
  color: var(--color-error, #ed4956);
  border: 1px solid var(--color-error, #ed4956);
  padding: 0.5rem 1rem;
  border-radius: var(--border-radius-base, 4px);
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.btn-delete-account:hover:not(:disabled) {
  background-color: var(--color-error, #ed4956);
  color: #fff;
}

.btn-delete-account:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Postagens Styles */
.posts-section {
  margin-top: 3rem;
}
.posts-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color, #333);
  padding-bottom: 1rem;
  margin-bottom: 2rem;
}
.posts-header h3 {
  font-weight: 400;
  margin: 0;
  font-size: 1.5rem;
}
.create-post-container {
  background: var(--bg-tertiary, #262626);
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  border: 1px solid var(--border-color, #333);
}
.preview-img-container {
  width: 150px;
  height: 150px;
  overflow: hidden;
  margin-bottom: 1rem;
  border-radius: 4px;
}
.preview-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}
.no-posts {
  text-align: center;
  color: var(--text-secondary, #a8a8a8);
  padding: 4rem 0;
  font-size: 1.1rem;
}
</style>
