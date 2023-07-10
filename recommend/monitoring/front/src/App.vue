<template>
  <div id="app">
    <b-card title="Card Title" no-body>
      <b-card-header header-tag="nav">
        <b-nav card-header tabs>
          <b-nav-item to="/" exact exact-active-class="active">수집 현황</b-nav-item>
          <b-nav-item to="/data" exact exact-active-class="active">데이터</b-nav-item>
          <b-nav-item to="/error" exact exact-active-class="active">에러</b-nav-item>
        </b-nav>
      </b-card-header>

      <b-card-body>
        <router-view></router-view>
      </b-card-body>
    </b-card>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  components: {
  }
}
</script>
