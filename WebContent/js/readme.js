<script src="jquery.js" type="text/javascript"></script>    

<textarea id="txtArea" style="width: 300px; height:100px">
    This is a test. A test, i say. The word TEST is mentioned three times.
</textarea>

<p>
    <label for="termSearch">Search</label>
    <input type="text" id="termSearch" name="termSearch" value="test" /><br/>
    <label for="termReplace">Replace</label>
    <input type="text" id="termReplace" name="termReplace" value="solution" /><br/>
    <label for="caseSensitive">Case Sensitive</label>
    <input type="checkbox" name="caseSensitive" id="caseSensitive" /><br/>
    <a href="#" onclick="SAR.find(); return false;" id="find">FIND</a><br/>
    <a href="#" onclick="SAR.findAndReplace(); return false;" id="findAndReplace">FIND/REPLACE</a><br/>
    <a href="#" onclick="SAR.replaceAll(); return false;" id="replaceAll">REPLACE ALL</a><br/>
</p>

<script type="text/javascript">
    var SAR = {};

    SAR.find = function(){
        // collect variables
        var txt = $("#txtArea").val();
        var strSearchTerm = $("#termSearch").val();
        var isCaseSensitive = ($("#caseSensitive").attr('checked') == 'checked') ? true : false;

        // make text lowercase if search is supposed to be case insensitive
        if(isCaseSensitive == false){
            txt = txt.toLowerCase();
            strSearchTerm = strSearchTerm.toLowerCase();
        }

        // find next index of searchterm, starting from current cursor position
        var cursorPos = ($("#txtArea").getCursorPosEnd());
        var termPos = txt.indexOf(strSearchTerm, cursorPos);

        // if found, select it
        if(termPos != -1){
            $("#txtArea").selectRange(termPos, termPos+strSearchTerm.length);
        }else{
            // not found from cursor pos, so start from beginning
            termPos = txt.indexOf(strSearchTerm);
            if(termPos != -1){
                $("#txtArea").selectRange(termPos, termPos+strSearchTerm.length);
            }else{
                alert("not found");
            }
        }
    };

    SAR.findAndReplace = function(){
        // collect variables
        var origTxt = $("#txtArea").val(); // needed for text replacement
        var txt = $("#txtArea").val(); // duplicate needed for case insensitive search
        var strSearchTerm = $("#termSearch").val();
        var strReplaceWith = $("#termReplace").val();
        var isCaseSensitive = ($("#caseSensitive").attr('checked') == 'checked') ? true : false;
        var termPos;

        // make text lowercase if search is supposed to be case insensitive
        if(isCaseSensitive == false){
            txt = txt.toLowerCase();
            strSearchTerm = strSearchTerm.toLowerCase();
        }

        // find next index of searchterm, starting from current cursor position
        var cursorPos = ($("#txtArea").getCursorPosEnd());
        var termPos = txt.indexOf(strSearchTerm, cursorPos);
        var newText = '';

        // if found, replace it, then select it
        if(termPos != -1){
            newText = origTxt.substring(0, termPos) + strReplaceWith + origTxt.substring(termPos+strSearchTerm.length, origTxt.length)
            $("#txtArea").val(newText);
            $("#txtArea").selectRange(termPos, termPos+strReplaceWith.length);
        }else{
            // not found from cursor pos, so start from beginning
            termPos = txt.indexOf(strSearchTerm);
            if(termPos != -1){
                newText = origTxt.substring(0, termPos) + strReplaceWith + origTxt.substring(termPos+strSearchTerm.length, origTxt.length)
                $("#txtArea").val(newText);
                $("#txtArea").selectRange(termPos, termPos+strReplaceWith.length);
            }else{
                alert("not found");
            }
        }
    };

    SAR.replaceAll = function(){
        // collect variables
        var origTxt = $("#txtArea").val(); // needed for text replacement
        var txt = $("#txtArea").val(); // duplicate needed for case insensitive search
        var strSearchTerm = $("#termSearch").val();
        var strReplaceWith = $("#termReplace").val();
        var isCaseSensitive = ($("#caseSensitive").attr('checked') == 'checked') ? true : false;

        // make text lowercase if search is supposed to be case insensitive
        if(isCaseSensitive == false){
            txt = txt.toLowerCase();
            strSearchTerm = strSearchTerm.toLowerCase();
        }

        // find all occurances of search string
        var matches = [];
        var pos = txt.indexOf(strSearchTerm);
        while(pos > -1) {
            matches.push(pos);
            pos = txt.indexOf(strSearchTerm, pos+1);
        }

        for (var match in matches){
            SAR.findAndReplace();
        }
    };


    /* TWO UTILITY FUNCTIONS YOU WILL NEED */
    $.fn.selectRange = function(start, end) {
        return this.each(function() {
            if(this.setSelectionRange) {
                this.focus();
                this.setSelectionRange(start, end);
            } else if(this.createTextRange) {
                var range = this.createTextRange();
                range.collapse(true);
                range.moveEnd('character', end);
                range.moveStart('character', start);
                range.select();
            }
        });
    };

    $.fn.getCursorPosEnd = function() {
        var pos = 0;
        var input = this.get(0);
        // IE Support
        if (document.selection) {
            input.focus();
            var sel = document.selection.createRange();
            pos = sel.text.length;
        }
        // Firefox support
        else if (input.selectionStart || input.selectionStart == '0')
            pos = input.selectionEnd;
        return pos;
    };  
</script>