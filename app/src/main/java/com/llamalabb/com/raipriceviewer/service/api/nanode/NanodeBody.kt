package com.llamalabb.com.raipriceviewer.service.api.nanode

/**
 * Created by andy on 2/7/18.
 */
object NanodeBody {
    fun AccountBalance(account: String): HashMap<String, String>{
        return hashMapOf(Pair("action","account_balance"), Pair("account", account))
    }
}