<!-- Svelte 4.2.11 -->

<script lang="ts">
  import Icon from "@iconify/svelte";

  export let color: string = '179, 198, 255'
  export let icon: string
  export let href: string | undefined
  export let isSmall: boolean = false

  // Manually positioned icons
  const iconPos = [
      [1, 0.5, 2],
      [6, 2, 1.5],
      [-0.5, 4.5, 1.3],
      [5, -0.5],
      [3.5, 4.5],
      [9.5, 0.3, 1.2],
      [12.5, 2.5, 0.8],
      [10, 4.4, 0.8],
  ]
</script>

<a class="action-card" class:small={isSmall} style="--card-color: {color}" on:click {href} role="button" tabindex="0" on:keydown>
  <slot/>

  <div class="icons">
    {#each iconPos as [x, y, size], i}
      <Icon icon={icon} style={`top: ${y}rem; right: ${x}rem; font-size: ${size || 1}em`} />
    {/each}
  </div>
</a>

<style lang="sass">
  @use '../vars'

  .action-card
    overflow: hidden
    display: block
    padding: 1rem
    border-radius: vars.$border-radius
    box-shadow: 0 5px 5px 1px vars.$c-shadow
    transition: all 0.2s ease
    cursor: pointer
    position: relative
    background: linear-gradient(45deg, transparent 20%, rgba(var(--card-color), 0.5) 100%)
    outline: 1px solid transparent
    filter: drop-shadow(0 0 12px rgba(var(--card-color), 0))
    color: rgba(255, 255, 255, 0.78)

    &:hover
      box-shadow: 0 0 0.5rem 0.2rem vars.$c-shadow
      transform: translateY(-3px)

      // Drop shadow glow
      filter: drop-shadow(0 0 12px rgba(var(--card-color), 0.5))
      outline-color: rgba(var(--card-color), 0.5)

    :global(span), &.small
      font-size: 1.2rem
      display: block
      margin-bottom: 0.5rem
      color: color-mix(in oklab, rgb(var(--card-color)) 25%, rgba(255, 255, 255, 0.75))

    .icons
      position: absolute
      inset: 0
      color: rgba(var(--card-color), 0.5)
      font-size: 2rem
      transition: all 0.2s ease
      z-index: -1
      mask-image: linear-gradient(45deg, transparent 30%, rgba(255,255,255,0.5) 70%, white 100%)
      opacity: 0.8

      @media (max-width: vars.$w-mobile)
        opacity: 0.6

      :global(> svg)
        position: absolute
        rotate: 20deg
</style>
