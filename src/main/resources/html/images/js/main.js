$(function(){
	$(window).resize(function(){
		$('#list_body').height($(window).height() - $('#search_box').height() - $('#list_footer').height());
	}).resize();
	if(document.search_from){
		$('#search_from').newforms();
		$('#batch_from').newforms();
	}
	

	
});
window.app = {
	about : function(){
		if(!this.inApp())return;
		javaApp.about();
		//javaApp.alert('test');
	},
	getInitData : function(){
		if(!this.inApp())return null;
		return javaApp.getInitData();
	},
	openMovFile : function(){
		if(!this.inApp())return false;
		return javaApp.openMovFile();
	},
	copyClipboard : function(str){
		if(!this.inApp())return false;
		return javaApp.copyClipboard(str);
	},
	downArchiveFile : function(data){
		if(!this.inApp())return false;
		return javaApp.downArchiveFile(data);
	},
	inApp : function(){
		return (typeof(javaApp)=='object' && typeof(javaApp.test) == 'function')
	}	
};

function json_encode(data){
	return JSON.stringify(data);
}
function json_decode(data){
	////data = $.parseJSON(data);
	return JSON.parse(data);
}

function ajax_jsonp(url, data, succ_fun, fail_fun){
	my_loading_show();
	$.ajax({
		type : "POST",
		url : serverUrl+url,
		data : data,
		dataType : "jsonp",
		success : function(data) {
			my_loading_hide();
			succ_fun && succ_fun(data);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			my_loading_hide();
			if(fail_fun){
				fail_fun(XMLHttpRequest, textStatus, errorThrown);
			}else{
				my_alert('网络异常');
			}
		}
	});
}

/**
设置列表批量选择框
*/
function set_select_all(id, bat_name){
	$(id).change(function (){
		var _this = this;
		$('input[name="'+bat_name+'"]').each(function (){
			this.checked = _this.checked;
			$(this).trigger('nf_change');
		})
	});
	var bat_list = $('input[name="'+bat_name+'"]');
	bat_list.change(function (){
		var _this = this;
		console.log(bat_list.filter('[checked]').length);
		if(bat_list.filter('[checked]').length == bat_list.length){
			$(id).get(0).checked = true;
			$(id).trigger('nf_change');
		}else{
			$(id).get(0).checked = false;
			$(id).trigger('nf_change');
		}
	});
}
