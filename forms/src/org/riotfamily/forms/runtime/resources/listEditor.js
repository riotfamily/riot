var initListEditor = function(id) {
    var el = $(id);
    var order = $(id + '-order');
    
    var tr = '#'+id+'>li>table>tbody>tr';
    
    $$(tr+'>td.button div', tr+'>td>input.button').each(function(b) {
        var td = b.tagName == 'TD' ? b : b.up('td');
        b.onmouseover = td.addClassName.bind(td, 'hover');
    	b.onmouseout = td.removeClassName.bind(td, 'hover');
    });
    var toggle = function(el, className, state) {
    	el[(state ? 'add' : 'remove') + 'ClassName'](className);
    }
    var updateClassNames = function() {
    	$$(tr+'>td.up div').each(function(b, i) {
    		toggle(b, 'disabled', i == 0);
        });
    	var down = $$(tr+'>td.down div');
    	var last = down.length-1;
    	down.each(function(b, i) {
    		toggle(b, 'disabled', i == last);
        });
        if (order) order.value = $$('#' + id + '>li').pluck('id').join(',') + ',';
    };
    
    $$(tr+'>td.up div').invoke('observe', 'click', function() {
        this.removeClassName('hover');
        var li = this.up('li');
        var prev = li.previous('li');
        if (prev) prev.insert({before: li.remove()});
        updateClassNames();
        if (li.viewportOffset()[1] < 0) li.scrollTo();
    });
    $$(tr+'>td.down div').invoke('observe', 'click', function() {
        this.removeClassName('hover');
        var li = this.up('li');
        var next = li.next('li');
        if (next) next.insert({after: li.remove()});
        updateClassNames();
        if (li.viewportOffset()[1] > document.viewport.getHeight()) li.scrollTo();
    });
    updateClassNames();
};
