(function ($) {

    $.fn.TreeMap = function (options) {
        if (!options || !options.data || options.data.CounterMan.length == 0) {
            alert("no data for tree draw");
            return;
        }
        if (!options.element) {
            alert("please set chart element");
            return;
        }
        if (!$.fn.jOrgChart) {
            alert("please refenerce  jOrgChart.js");
            return;
        }
        var ul = DrawItem(options.data);
        this.append(ul);
        $(this).jOrgChart({ chartElement: options.element });




        function DrawItem(dataItem) {
            var className;
            if (dataItem.XValue < 65) {
                if (dataItem.XValue > 40) className = "Node-More40";
                else if (dataItem.XValue < 21) className = "Node-no21";
            }
            if (className == null) className = "Node-Number";
            var ItemClassName;
            if (dataItem.Status == -1) ItemClassName = "map-item-off"
            else ItemClassName = "map-item-normal";
            var itemObj = $("<li><div class='" + ItemClassName + "' mid='"+ dataItem.ID +"'>" + dataItem.Name + " <span class='" + className + "'>" + dataItem.XValue + "</span>" + "</div></li>");
            if (dataItem.CounterMan != null && dataItem.CounterMan.length > 0) {
                var ul = $("<ul></ul>");
                $(dataItem.CounterMan).each(function () {
                    ul.append(DrawItem(this));
                    dataItem.XValue += this.XValue;
                });
                itemObj.find("." + className).html(dataItem.XValue);
                itemObj.append(ul)
            };
            return itemObj;

        }


    }
})(jQuery);