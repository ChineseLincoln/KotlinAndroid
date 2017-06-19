package org.unreal.pay

import org.unreal.pay.alipay.AliPay
import org.unreal.pay.alipay.AliPayImplement
import org.unreal.pay.union.UnionBankPay
import org.unreal.pay.union.UnionBankPayImplement
import org.unreal.pay.weixin.WeiXinPay
import org.unreal.pay.weixin.WeiXinPayImplement

interface PayOrder

fun payment(payOrder :PayOrder, onSuccess: () -> Unit, onError: (String) -> Unit): PayFunction {
    when (payOrder) {
        is WeiXinPay -> {
            return WeiXinPayImplement(payOrder, onSuccess, onError)
        }
        is UnionBankPay -> {
            return UnionBankPayImplement(payOrder, onSuccess, onError)
        }
        is AliPay -> {
            return AliPayImplement(payOrder, onSuccess, onError)
        }
        else -> throw IllegalArgumentException("Payment methods are not supported")
    }

}



