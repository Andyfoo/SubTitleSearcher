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
		var title;
		if(typeof(last_list_table_tr) != 'undefined' && last_list_table_tr.length > 0){
			title = last_list_table_tr.find('.title').text();
			if(title){
				title = title.trim();
				if(title.length > 100){
					title = title.substring(0,100);
				}
				menu.push('|');
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
                ContextMenu.render(e,menu,this); //开始渲染
        });
	$('.list_table').on('mouseenter','tr',function(){
		window.last_list_table_tr = $(this);
	});
	$('.list_table').on('click','.download',function(){
		var btn = this;
		btn.disabled = true;
		var tr = $(this).parents('tr');
		down_zimu(tr.find('input[name="item_id"]').val(), tr.find('select[name="item_simplified_charset"]').val());
		window.setTimeout(function(){
			btn.disabled = false;
		}, 300);
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
	
	$('#btn_download').on('click', function(){
		var btn = this;
		btn.disabled = true;
		
		var items_ids = [];
		
		var item_simplified_charset = $('select[name="item_simplified_charset"]');
		$('input[type="checkbox"][name="item_id"]').each(function(index){
			var charset = item_simplified_charset.eq(index).val();
			var title = $('.list_table .title').eq(index).attr('val');
			if(this.checked){
				items_ids.push({
					id:this.value,
					filenameType:1,
					title:title,
					simplified:charset.length>0,
					charset:charset
				});
			}
		});
		if(items_ids.length == 0){
			btn.disabled = false;
			my_alert('请选择字幕');
			return;
		}

		var searchData = {
			items : items_ids
		};
		ajax_jsonp('/extract_api/down_archive_file',{
			data : json_encode(searchData)
		}, function (data){
			btn.disabled = false;
			if(data.result > 0){
				my_alert(data.message);
				return;
			}
			if(data == null){
				my_alert('初始化数据失败');
				return;
			}
			if(data.saveSelected){
				my_tip('下载成功');
			}else{
				my_tip('下载失败');
			}
		});
	});

	//window.serverPort = serverPort;
	window.serverUrl = "http://127.0.0.1:"+serverPort;
	laytpl_preload('#rec_list_tpl');
	init_data();
});

function init_data(){
	ajax_jsonp('/extract_api/get_init_data',{
		
	}, function (data){
		if(data.result > 0){
			my_alert(data.message);
			return;
		}
		if(data == null){
			my_alert('初始化数据失败');
			return;
		}
		
		$('#search_box .con').html(data.title + '<span class="light">('+data.archiveExt+', '+data.archiveSizeF+')</span>');
		
		$('#status_label').html('压缩包里共有'+data.list.length+'个字幕文件');
		$('#rec_list').empty();
		laytpl_render('#rec_list_tpl', data, function(html){
			$('#rec_list').append(html);
			$('#batch_from').newforms();
		});
	});
}
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
	var title =  $('.list_table .title').eq(id).attr('val');
	items_ids.push({
		id:id,
		filenameType:0,
		title:title,
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
	ajax_jsonp('/extract_api/down_archive_file',{
		data : json_encode(searchData)
	}, function (data){
		if(data.result > 0){
			my_alert(data.message);
			return;
		}
		if(data == null){
			my_alert('初始化数据失败');
			return;
		}
		if(data.saveSelected){
			my_tip('下载成功');
		}else{
			my_tip('下载失败');
		}
	});
}
