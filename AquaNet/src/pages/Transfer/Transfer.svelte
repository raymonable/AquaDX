<script lang="ts">
  import { slide } from "svelte/transition";
  import { ts } from "../../libs/i18n";
  import TransferServer from "./TransferServer.svelte";
  import { DATA_HOST } from "../../libs/config";


  let tabs = ['chu3', 'mai2', 'ongeki']
  let game: Record<string, { game: string, version: string }> = {
    'chu3': { game: "SDHD", version: "2.30" },
    'mai2': { game: "SDGA", version: "1.50" },
    'ongeki': { game: "SDDT", version: "1.45" }
  }
  let tab = 0

  let src = JSON.parse(localStorage.getItem('src') ?? JSON.stringify({
    dns: "",
    card: "",
    keychip: ""
  }))
  let dst = JSON.parse(localStorage.getItem('dst') ?? `{ card: "", server: "", keychip: "" }`)
  let gameInfo = JSON.parse(localStorage.getItem('gameInfo') ?? JSON.stringify({
    game: "",
    version: "",
  }))

  function defaultGame() {
    gameInfo.game = game[tabs[tab]].game
    gameInfo.version = game[tabs[tab]].version
  }

  function onChange() {
    localStorage.setItem('src', JSON.stringify(src))
    localStorage.setItem('dst', JSON.stringify(dst))
    localStorage.setItem('gameInfo', JSON.stringify(gameInfo))
  }

  defaultGame()
</script>

<main class="content">
  <div class="outer-title-options">
    <h2>🏳️‍⚧️ AquaTrans™ Data Transfer?</h2>
    <nav>
      {#each tabs as tabName, i}
        <div transition:slide={{axis: 'x'}} class:active={tab === i}
             on:click={() => tab = i} on:keydown={e => e.key === 'Enter' && (tab = i)}
             role="button" tabindex="0">
          {ts(`settings.tabs.${tabName}`)}
        </div>
      {/each}
    </nav>
  </div>

  <div class="prompt">
    <p>👋 Welcome to the AquaTrans™ server data transfer tool!</p>
    <p>You can use this to export data from any server, and input data into any server using the connection credentials (card number, server address, and keychip id).</p>
    <p>This tool will simulate a game client and pull your data from the source server, and push your data to the destination server.</p>
    <p>Please fill out the form below to get started!</p>
  </div>

  <TransferServer bind:src={src} bind:gameInfo={gameInfo} on:change={onChange} />
  <div class="arrow"><img src="{DATA_HOST}/d/DownArrow.png" alt="arrow"></div>
  <TransferServer bind:src={dst} bind:gameInfo={gameInfo} on:change={onChange}
    isSrc={false} />
</main>

<style lang="sass">
  .arrow
    width: 100%
    display: flex
    justify-content: center
    margin-top: -40px
    margin-bottom: -40px
    z-index: 0

  // CSS animation to let the image opacity breathe
  .arrow img
    animation: breathe 1s infinite alternate

  @keyframes breathe
    0%
      opacity: 0.5
    100%
      opacity: 1
</style>


