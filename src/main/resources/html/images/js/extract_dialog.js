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
		var tr = $(this).parents('tr');
		down_zimu(tr.find('input[name="item_id"]').val(), tr.find('select[name="item_simplified_charset"]').val());
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
		var _this = this;
		this.disabled = true;
		
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
			my_alert('请选择字幕');
			return;
		}

		var searchData = {
			items : items_ids
		};
		my_loading_show();
		if(app.downArchiveFile(json_encode(searchData))){
			my_tip('下载成功');
		}else{
			my_tip('下载失败');
		}
		my_loading_hide();
		window.setTimeout(function(){
			_this.disabled = false;
		}, 300);
	});
	
	laytpl_preload('#rec_list_tpl');
	init_data();
});
function init_data(){
	my_loading_show();
	var data = app.getInitData();
	if(data == null){
		my_alert('初始化数据失败');
		return;
	}
	data = json_decode(data);
	
	$('#search_box .con').html(data.title + '<span class="light">('+data.archiveExt+', '+data.archiveSizeF+')</span>');
	
	$('#status_label').html('压缩包里共有'+data.list.length+'个字幕文件');
	$('#rec_list').empty();
	laytpl_render('#rec_list_tpl', data, function(html){
		$('#rec_list').append(html);
		$('#batch_from').newforms();
	});
	
	my_loading_hide();
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
	my_loading_show();
	if(app.downArchiveFile(json_encode(searchData))){
		my_tip('下载成功');
	}else{
		my_tip('下载失败');
	}
	my_loading_hide();
}
