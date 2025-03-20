<script lang="ts">
  import StatusOverlays from "../../components/StatusOverlays.svelte";
  import { TRANSFER } from "../../libs/sdk";
  import InputTextShort from "./InputTextShort.svelte";

  export let src: AllNetSrc
  export let gameInfo: AllNetGame
  export let isSrc: boolean = true

  let tested: boolean = false
</script>

<div class="server source" class:src={isSrc}>
  <h3>{isSrc ? "Source" : "Target"} Server</h3>

  <!-- First input line -->
  <div class="inputs">
    <InputTextShort desc="Server Address" placeholder="e.g. http://aquadx.hydev.org"
      bind:value={src.dns} on:change validate={v => /^https?:\/\/[a-z0-9.-]+(:\d+)?$/i.test(v)} disabled={tested} />
    <InputTextShort desc="Keychip ID" placeholder="e.g. A0299792458"
      bind:value={src.keychip} on:change validate={v => /^[A-Z0-9]{11}$/.test(v)} disabled={tested} />
  </div>

  <!-- Second input line -->
  <div class="inputs">
    <div class="game-version">
      <InputTextShort desc="Game" placeholder="e.g. SDHD"
        bind:value={gameInfo.game} on:change disabled={tested} />
      <InputTextShort desc="Version" placeholder="e.g. 2.30"
        bind:value={gameInfo.version} on:change disabled={tested} />
    </div>
    <InputTextShort desc="Card Number" placeholder="e.g. 27182818284590452353"
      bind:value={src.card} on:change disabled={tested} />
  </div>

  <!-- Buttons -->
  <div class="inputs buttons">
    {#if !tested}
      <button class="flex-1" on:click={() => {}}>Test Connection</button>
    {:else}
      <button class="flex-1" on:click={() => {}}>Export Data</button>
    {/if}
  </div>
</div>

<style lang="sass">
  @use "../../vars"
  @use "sass:color"

  .server
    display: flex
    flex-direction: column
    gap: 1rem

    --c-src: 255, 174, 174
    &.src
      --c-src: 173, 192, 247

    padding: 1rem
    border-radius: vars.$border-radius
    // background-color: vars.$ov-light
    background: #252525

    // Pink outline
    border: 1px solid rgba(var(--c-src), 0.5)
    box-shadow: 0 0 1rem 0 rgba(var(--c-src), 0.25)

    h3
      margin: 0
      font-size: 1.5rem
      text-align: center


  .inputs
    display: flex
    flex-wrap: wrap
    gap: 1rem

    .game-version
      flex: 60
      display: flex
      gap: 1rem

      :global(> *)
        width: 100px

    &.buttons
      margin-top: 1rem
</style>
