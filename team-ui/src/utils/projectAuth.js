import Cookies from 'js-cookie'

const TokenKey = 'Project-Id'

export function getProjectToken() {
    return Cookies.get(TokenKey)
}

export function setProjectToken(token) {
    return Cookies.set(TokenKey, token)
}

export function removeProjectToken() {
    return Cookies.remove(TokenKey)
}
