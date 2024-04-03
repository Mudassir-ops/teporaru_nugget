package com.aioapp.nuggetmvp.utils

import com.aioapp.nuggetmvp.utils.enum.ScreenState

var wakeupCallBack: ((Boolean) -> Unit)? = null
var actionCallBack: ((Boolean) -> Unit)? = null
//var screenStateUpdateCallback: ((ScreenState) -> Unit)? = null