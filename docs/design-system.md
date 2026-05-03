# Design System (Instagram Web)

Este documento especifica a regra visual e identidade padrão da aplicação. Faremos uso exclusivo de propriedades customizadas do CSS (CSS Variables) centralizadas em um único arquivo (`base.css`). O objetivo disso é dar um grande poder de manutenção e controle para o usuário permitir que, no futuro, o visual inteiro de uma página mude com apenas a alteração de um código Hexadecimal.

## 1. Tema Dominante: Dark Mode Premium
O tema atual adotado é o Escuro para forçar um apelo mais premium, luxuoso e menos exauriente a tela, porém os tons precisam evitar o "preto #000000 puro", priorizando camadas de profundidade com cinza-escuro.

## 2. Tipografia Global
Acessível, moderna e universal. A prioridade de importação de fontes do Sistema será respeitada nessa exata ordem de cascata:
- Padrão **Headings & Body**: `'Inter', system-ui, -apple-system, sans-serif`
- Peso de Títulos (h1, h2): **600** a **700** (Semibold, Bold).
- Peso de Corpo (p, links): **400** a **500** (Regular, Medium).

## 3. Paleta de Cores e Feedback (CSS Tokens)
Todos as cores usadas no ecossistema (seja na página principal, inputs ou botões) seguirão exatos *Tokens Centrais*. Nenhum componente Vue deve usar uma cor "hardcoded".

- `--bg-primary`: **#121212** (Fundo Escuro Principal da Aplicação)
- `--bg-secondary`: **#1E1E1E** (Fundo de Cards, Modais, Inputs em elevação)
- `--bg-tertiary`: **#2A2A2A** (Pequenos badges, estados de Hover focados)

**Cores de Conteúdo (Texto/Ícone):**
- `--text-primary`: **#F5F5F5** (Texto principal para contrastar com fundos ultra-escuros)
- `--text-secondary`: **#A0A0A0** (Efeito "Muted" usado nas legendas e placeholders).
- `--border-color`: **#333333** (Bordas divisórias, separadores suaves, contorno de campos text).

**Cores Temáticas de Ação (Ajustável no futuro):**
- `--color-brand`: **#6E24FF** / **#FF1493** (Cor Viva primária. Neste setup dark, podemos usar algo mais exótico). *Atual configuração foi ajustada para Azul Brilhante ou Fúcsia Premium para criar contraste vibrante com o escuro*.  Atualmente setado p/ Instagram Vibe: `--color-brand: #E1306C`
- `--color-brand-hover`: `--color-brand` ligeiramente escurecido/esclarecido.

**Painel de Feedback (Formulários/Erros):**
- `--color-error`: **#FF4D4F** (Texto ou Borda de feedback para falhas)
- `--color-error-bg`: **rgba(255, 77, 79, 0.1)** (Fundo opaco de caixas de erro)
- `--color-success`: **#52C41A** (Toast e Ícones de sucesso)

## 4. UI Estética e Interativa
Qualquer transição, foco em botão de envio ou hover de ícone deve carregar um comportamento responsivo visual com a propriedade de transição padrão:

`<style>`
`transition: all 0.3s ease;`
`</style>`

E bordões padrão para interfaces arredondadas e não-pontiagudas nos Inputs e Containers de formulário:
`border-radius: 8px /* ou 12px */;`
