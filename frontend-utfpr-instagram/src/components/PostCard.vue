<script setup>
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const props = defineProps({
  post: {
    type: Object,
    required: true
  },
  isOwner: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['curtir', 'deletar', 'atualizar'])

const authStore = useAuthStore()
const isLikedByMe = computed(() => {
  if (!authStore.user || !props.post.curtidasIds) return false
  return props.post.curtidasIds.includes(authStore.user.id.toString())
})

const isEditing = ref(false)
const editLegenda = ref(props.post.legenda)

const handleCurtir = () => {
  emit('curtir', props.post.id)
}

const handleDelete = () => {
  if (window.confirm("Deseja realmente excluir este post?")) {
    emit('deletar', props.post.id)
  }
}

const toggleEdit = () => {
  isEditing.value = !isEditing.value
  editLegenda.value = props.post.legenda
}

const saveEdit = () => {
  if (editLegenda.value.length < 5 || editLegenda.value.length > 200) {
    alert('A legenda deve ter entre 5 e 200 caracteres.')
    return
  }
  emit('atualizar', props.post.id, editLegenda.value)
  isEditing.value = false
}

const cancelEdit = () => {
  isEditing.value = false
}
</script>

<template>
  <div class="post-card">
    <div class="post-image-container">
      <!-- Caso a imagem tenha o prefixo ou nao -->
      <img 
        :src="post.imagem.startsWith('data:image') ? post.imagem : `data:image/jpeg;base64,${post.imagem}`" 
        alt="Imagem do Post" 
        class="post-image" 
      />
    </div>

    <div class="post-actions">
      <button class="action-btn like-btn" @click="handleCurtir" title="Curtir" :class="{ 'liked': isLikedByMe }">
        <svg viewBox="0 0 24 24" :fill="isLikedByMe ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2" class="icon-heart">
          <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
        </svg>
      </button>
      <span class="likes-count">{{ post.curtidas }} {{ post.curtidas == 1 ? 'curtida' : 'curtidas' }}</span>

      <div class="owner-actions" v-if="isOwner">
        <button class="action-btn" @click="toggleEdit" title="Editar Legenda">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-edit">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
          </svg>
        </button>
        <button class="action-btn delete-btn" @click="handleDelete" title="Excluir Post">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="icon-delete">
            <polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
          </svg>
        </button>
      </div>
    </div>

    <div class="post-content">
      <div v-if="!isEditing" class="caption-display">
        <span class="caption-text">{{ post.legenda }}</span>
      </div>
      
      <div v-else class="caption-edit">
        <textarea 
          v-model="editLegenda" 
          rows="2" 
          class="edit-textarea"
          placeholder="Escreva uma nova legenda..."
        ></textarea>
        <div class="edit-controls">
          <button class="btn-save" @click="saveEdit">Salvar</button>
          <button class="btn-cancel" @click="cancelEdit">Cancelar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.post-card {
  background-color: var(--bg-secondary, #111);
  border: 1px solid var(--border-color, #333);
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 2rem;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.post-image-container {
  width: 100%;
  aspect-ratio: 1 / 1;
  background-color: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-bottom: 1px solid var(--border-color, #333);
}

.post-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-actions {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  gap: 0.5rem;
}

.action-btn {
  background: none;
  border: none;
  color: var(--text-primary, #fff);
  cursor: pointer;
  padding: 0.25rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.2s, color 0.2s;
}

.action-btn:hover {
  color: var(--text-secondary, #a8a8a8);
}

.action-btn:active {
  transform: scale(0.9);
}

.like-btn:hover {
  color: #ed4956;
}

.like-btn.liked {
  color: #ed4956;
}

.icon-heart, .icon-edit, .icon-delete {
  width: 24px;
  height: 24px;
}

.likes-count {
  font-weight: 600;
  font-size: 0.95rem;
  margin-left: 0.25rem;
}

.owner-actions {
  margin-left: auto;
  display: flex;
  gap: 0.5rem;
}

.delete-btn:hover {
  color: #ed4956;
}

.post-content {
  padding: 0 1rem 1rem 1rem;
}

.caption-text {
  font-size: 0.95rem;
  line-height: 1.4;
  word-break: break-word;
}

.caption-edit {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.edit-textarea {
  width: 100%;
  background-color: var(--bg-tertiary, #262626);
  border: 1px solid var(--border-color, #333);
  color: var(--text-primary, #fff);
  border-radius: 4px;
  padding: 0.5rem;
  font-size: 0.95rem;
  resize: vertical;
  outline: none;
}

.edit-textarea:focus {
  border-color: var(--color-brand, #0095f6);
}

.edit-controls {
  display: flex;
  gap: 0.5rem;
  justify-content: flex-end;
}

.btn-save, .btn-cancel {
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.85rem;
  border: none;
}

.btn-save {
  background-color: var(--color-brand, #0095f6);
  color: #fff;
}

.btn-save:hover {
  background-color: var(--color-brand-hover, #1877f2);
}

.btn-cancel {
  background-color: transparent;
  color: var(--text-primary, #fff);
  border: 1px solid var(--border-color, #333);
}

.btn-cancel:hover {
  background-color: var(--bg-tertiary, #262626);
}
</style>
