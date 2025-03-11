<script lang="ts">
  export let desc: string
  export let value: string
  export let placeholder: string
  export let flex: number = 60
</script>

<div class="field" style="flex: {flex}">
  <label for={desc}>{desc}</label>
  <input type="text" placeholder={placeholder} bind:value={value} id="{desc}" on:change />
</div>

<style lang="sass">
  .field
    display: inline-flex
    flex-direction: column
    gap: 0.5rem

    label
      font-weight: bold
</style>
