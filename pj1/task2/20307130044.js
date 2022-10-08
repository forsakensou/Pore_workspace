Java.perform(function(){
	var handlerstring = "com.meishichina.android.activity.WelcomeActivity$b";
   	var clazz = Java.use("com.meishichina.android.activity.WelcomeActivity");
	console.log("Frida js start!")
   	clazz.b.overload("boolean").implementation = function(arg6){
      	Java.choose(handlerstring, 
			{onMatch:function(instance){instance.sendEmptyMessageDelayed(1, 1);},
			onComplete: function(){send("complete");}}
     	);
      	send("overload of b(boolean) start!");
   	};
});