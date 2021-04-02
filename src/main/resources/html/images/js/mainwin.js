//alert(navigator.userAgent);
$(function(){
	set_select_all('#select_all', 'item_id');
	
	$('.list_table').contextmenu(function (e) {
		var sel_text = window.getSelection().toString();
		var menu=[
			[
				'下载字幕',
				function (dom) {
					down_last_tr_zimu();
				}
			],
			'|',
			[
				'下载字幕(UTF-8 繁转简)',
				function (dom) {
					down_last_tr_zimu('UTF-8');
				}
			],
			[
				'下载字幕(GBK 繁转简)',
				function (dom) {
					down_last_tr_zimu('GBK');
				}
			],
			[
				'下载字幕(Big5 繁转简)',
				function (dom) {
					down_last_tr_zimu('Big5');
				}
			]
		];
		if(typeof(last_list_table_tr) == 'undefined' || last_list_table_tr.find('.download[url]').length>0){
			menu = [];
		}
		var title;
		if(typeof(last_list_table_tr) != 'undefined' && last_list_table_tr.length > 0){
			title = last_list_table_tr.find('.title').text();
			if(title){
				title = title.trim();
				if(title.length > 100){
					title = title.substring(0,100);
				}
				if(menu.length>0)menu.push('|');
				menu.push([
					'复制文字：'+title,
					function (dom) {
						app.copyClipboard(title);
					}
				]);
			}
		}
		if(sel_text){
			sel_text = sel_text.trim();
			if(sel_text.length > 100){
				sel_text = sel_text.substring(0,100);
			}
			if(title!=sel_text){
				menu.push([
					'复制文字：'+sel_text,
					function (dom) {
						app.copyClipboard(sel_text);
					}
				]);
			}
		}
		if(menu.length==0){
			return;
		}
                ContextMenu.render(e,menu,this); //开始渲染
        });
	$('.list_table').on('mouseenter','tr',function(){
		window.last_list_table_tr = $(this);
	});
	$('.list_table').on('click','.download',function(){
		var tr = $(this).parents('tr');

		if($(this).attr('url')){
			ajax_jsonp('/api/open_url',{
				url : $(this).attr('url')
			}, function (data){
				if(data.result > 0){
					my_alert(data.message);
					return;
				}
				
			});
			return;
		}
		down_zimu(tr.find('input[name="item_id"]').val(), tr.find('select[name="item_simplified_charset"]').val());
	});
	
	$('#btn_about').on('click', function(){
		app.about();
	});
	

	$('#simplified_select_all').change(function(){
		var checkbox = this;
		$('select[name="item_simplified_charset"]').each(function(){
			console.log(checkbox.checked)
			if($(this).val() == '' && checkbox.checked){
				$(this).val('UTF-8');
				$(this).trigger('nf_change');
			}else if(!checkbox.checked){
				$(this).val('');
				$(this).trigger('nf_change');
			}
		});
	});
	$('#btn_sel_file').on('click', function(){
		var _this = this;
		this.disabled = true;
		if(app.openMovFile()){
			init_data_fileinfo();
			search_start_thread();
		}
		window.setTimeout(function(){
			_this.disabled = false;
		}, 300);
	});
	$('#btn_search').on('click', function(){
		var _this = this;
		this.disabled = true;
		search_start_thread(true);
		window.setTimeout(function(){
			_this.disabled = false;
		}, 300);
	});
	$('#btn_download').on('click', function(){
		var _this = this;
		this.disabled = true;
		
		var items_ids = [];
		
		var item_simplified_charset = $('select[name="item_simplified_charset"]');
		$('input[type="checkbox"][name="item_id"]').each(function(index){
			var charset = item_simplified_charset.eq(index).val();
			if(this.checked){
				items_ids.push({
					id:this.value,
					filenameType:1,
					simplified:charset.length>0,
					charset:charset
				});
			}
		});
		if(items_ids.length == 0){
			my_alert('请选择字幕');
			return;
		}

		var searchData = {
			items : items_ids
		};
		ajax_jsonp('/api/zimu_down',{
			data : json_encode(searchData)
		}, function (data){
			if(data.result > 0){
				my_alert(data.message);
				return;
			}
			my_tip('下载完成');
		});
		
		window.setTimeout(function(){
			_this.disabled = false;
		}, 300);
	});
	
	laytpl_preload('#rec_list_tpl');
	init_data();
	search_start_thread();
});
//下载鼠标最后移动到的字幕
function down_last_tr_zimu(charset){
	if(typeof(last_list_table_tr) == 'undefined' || last_list_table_tr.length == 0){
		my_alert('数据错误');
		return;
	}
	down_zimu(last_list_table_tr.find('input[name="item_id"]').val(), charset);
}
//下载指定字幕
function down_zimu(id, charset){
	var items_ids = [];
	if(!charset){
		charset = $('select[name="item_simplified_charset"]').eq(id).val();
	}
	items_ids.push({
		id:id,
		filenameType:0,
		simplified:charset.length>0,
		charset:charset
	});
	if(items_ids.length == 0){
		my_alert('请选择字幕');
		return;
	}

	var searchData = {
		items : items_ids
	};
	ajax_jsonp('/api/zimu_down',{
		data : json_encode(searchData)
	}, function (data){
		if(data.result > 0){
			my_alert(data.message);
			return;
		}
		my_tip('下载完成');
	});
}
//java调用
function page_callback(){
	init_data_fileinfo();
	search_start_thread();
	return true;
}
function search_start_thread(is_click){
	window.setTimeout(function(){
		search_start(is_click);
	}, 100);
}
//开始搜索 
function search_start(is_click){
	if($('input[name="filename"]').val().length == 0){
		if(is_click){
			my_alert('请选择视频文件');
		}
		return;
	}
	
	var searchParm = {};
	var form_count = 0;
	$('input[name="froms"]').each(function(){
		var val = $(this).attr('value');
		searchParm['from_'+val] = this.checked;
		if(this.checked){
			form_count++;
		}
	});
	if(form_count == 0){
		my_loading_hide();
		if(is_click){
			my_alert('请选择至少1个字幕数据源');
		}
		return;
	}
	
	var items = [];
	
	var searchData = {
		searchParm : searchParm,
		items : items
	};
	var time1 = (new Date()).getTime();
	ajax_jsonp('/api/zimu_list',{
		data : json_encode(searchData)
	}, function (data){
		if(data.result > 0){
			my_alert(data.message);
			return;
		}
		var data = {
			list : data.list
		};
		var useTime = (((new Date()).getTime() - time1)/1000).toFixed(2);
		$('#status_label').html('查询到'+data.list.length+'个字幕数据，共耗时：'+(useTime)+'秒');
		$('#rec_list').empty();
		laytpl_render('#rec_list_tpl', data, function(html){
			$('#rec_list').append(html);
			$('#batch_from').newforms();
		});
	});
	
	//var dataStr = app.searchList(json_encode(searchData));


	
}
function init_data(){
	my_loading_show();
	var data = app.getInitData();
	if(data == null){
		my_alert('初始化数据失败');
		return;
	}
	
	
	data = json_decode(data);
	
	if(data.fileinfo && data.fileinfo.movFilename){
		$('input[name="filename"]').val(data.fileinfo.movFilename);
	}
	if(data.searchParm){
		var froms = ["sheshou", "xunlei", "zimuku", "subhd"];
		for(var i in froms){
			if(data.searchParm['from_'+froms[i]]){
				$('input[name="froms"][value="'+froms[i]+'"]')[0].checked = true;
				$('input[name="froms"][value="'+froms[i]+'"]').trigger('nf_change');
			}
		}
	}
	window.serverPort = data.serverPort;
	window.serverUrl = "http://127.0.0.1:"+serverPort;
	
	my_loading_hide();
}
function init_data_fileinfo(){
	my_loading_show();
	var data = app.getInitData();
	if(data == null){
		my_alert('初始化数据失败');
		return;
	}
	
	
	data = json_decode(data);
	
	if(data.fileinfo && data.fileinfo.movFilename){
		$('input[name="filename"]').val(data.fileinfo.movFilename);
	}
	my_loading_hide();
}

