  </div>
  <div id="footer">
    <p>The best little lotto U could get.</p>
  </div>
</div>
<%-- Include any requested javascript files --%>
<c:forTokens items="${includeJs}" delims=" " var="jsFile" varStatus="status">
    <c:if test="${fn:length(fn:trim(jsFile)) > 0}">
    <script type="text/javascript" src="<c:url value="/assets/${jsFile}"/>"></script>
    </c:if>
</c:forTokens>
<script type="text/javascript">
$(function() {
    $.lottoBaseUrl = "<c:url value="/"/>";
});
</script>
</body>
</html>