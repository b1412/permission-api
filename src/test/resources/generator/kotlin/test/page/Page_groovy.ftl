package customer.pages

import geb.Page
import org.openqa.selenium.Keys


class ${Utils.upperCamel(entity.name)}Page extends Page {

    // location go to
    static url = "http://test.ccfxtrader.com/pages/payees/payee-add"

    //define all elements
    static content = {
<#list entity.fields as f>
    ${Utils.upperCamel(f.name)}Input { $("${Utils.lowerCamel(f.name)}ID") }
</#list>
    }
    //define check point
    static at = { driver.currentUrl == url }
}
