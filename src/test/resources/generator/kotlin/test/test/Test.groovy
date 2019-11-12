package customer.tests

import customer.pages.LoginPage
import geb.spock.GebReportingSpec
import customer.pages.*
import spock.lang.Unroll

import static share.UserConfig.IndividualUser

class LoginTest extends GebReportingSpec {
    @Unroll
    def "test login use #username and #password in #page"(){
        given:
        to LoginPage
        LoginWith(username,password)

        expect:
        at page

        where:
        username | password | page
        "testIndividual222" | "vDktV36H" | LoginPage
        "testIndividual" | "12345678" | LoginPage
        "testIndividual222" | "12345678" | LoginPage
        "testUnverifyUser" | "gwSNJiaK" | LoginPage
        "testIndividual" | "vDktV36H" | HomePage
    }

}
