showAccounts = () ->
  $ ->
    $.get "/accounts", (data) ->
      strAppend = ""
      $.each data, (index, accountModel) ->
        strAppend = strAppend + "<li><a href='/accountViewMore?id="+accountModel.id+"'>" + accountModel.name + "</a></li>"
      $("#accounts").append strAppend
window.showAccounts = showAccounts