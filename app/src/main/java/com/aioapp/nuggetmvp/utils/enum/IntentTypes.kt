package com.aioapp.nuggetmvp.utils.enum

enum class IntentTypes(val label: String) {
    SHOW_MENU("show menu"),
    ADD("add"),
    REMOVE("remove"),
    SHOW_CART("show cart"),
    PLACE_ORDER("place order"),
    NEEDS_EXTRA("need extra"),
    REFILL_DRINK("refill drink"),
    PAYMENT("payment"),
    INVALID("invalid"),
    AFFIRM("affirm"),
    DENY("deny")
}