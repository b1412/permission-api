package com.github.b1412.security.custom

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.util.Assert

class CustomUserDetailsAuthenticationProvider(private val passwordEncoder: PasswordEncoder, private val userDetailsService: CustomUserDetailsService) : AbstractUserDetailsAuthenticationProvider() {

    /**
     * The password used to perform
     * [PasswordEncoder.matches] on when the user is
     * not found to avoid SEC-2056. This is necessary, because some
     * [PasswordEncoder] implementations will short circuit if the password is not
     * in a valid format.
     */
    private var userNotFoundEncodedPassword: String? = null

    @Throws(AuthenticationException::class)
    override fun additionalAuthenticationChecks(userDetails: UserDetails, authentication: UsernamePasswordAuthenticationToken) {

        if (authentication.credentials == null) {
            logger.debug("Authentication failed: no credentials provided")
            throw BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"))
        }

        val presentedPassword = authentication.credentials
                .toString()

        if (!passwordEncoder.matches(presentedPassword, userDetails.password)) {
            logger.debug("Authentication failed: password does not match stored value")
            throw BadCredentialsException(
                    messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"))
        }
    }

    @Throws(Exception::class)
    override fun doAfterPropertiesSet() {
        Assert.notNull(this.userDetailsService, "A UserDetailsService must be set")
        this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD)
    }

    override fun retrieveUser(username: String, authentication: UsernamePasswordAuthenticationToken): UserDetails {

        val auth = authentication as CustomAuthenticationToken
        val loadedUser: UserDetails?

        try {
            loadedUser = this.userDetailsService.loadUserByUsernameAndClientId(auth.principal
                    .toString(), auth.domain!!)
        } catch (notFound: UsernameNotFoundException) {
            if (authentication.getCredentials() != null) {
                val presentedPassword = authentication.getCredentials()
                        .toString()
                passwordEncoder.matches(presentedPassword, userNotFoundEncodedPassword)
            }
            throw notFound
        } catch (repositoryProblem: Exception) {
            throw InternalAuthenticationServiceException(repositoryProblem.message, repositoryProblem)
        }

        if (loadedUser == null) {
            throw InternalAuthenticationServiceException("UserDetailsService returned null, " + "which is an interface contract violation")
        }
        return loadedUser
    }

    companion object {

        /**
         * The plaintext password used to perform
         * PasswordEncoder#matches(CharSequence, String)}  on when the user is
         * not found to avoid SEC-2056.
         */
        private const val USER_NOT_FOUND_PASSWORD = "userNotFoundPassword"
    }

}