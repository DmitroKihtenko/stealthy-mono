<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ page import="top.secret.pojo.config.InstitutionConfig" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Common footer</title>
    <link href="<c:url value="static/css/backgrounds.css"/>" rel="stylesheet">
</head>
<body class="d-flex flex-column">
<footer class="flex-shrink-0 mt-auto bg-diluted text-center">
    <div class="container">
        <% InstitutionConfig info = new InstitutionConfig();
        try {
            info = ((InstitutionConfig) request.
                    getAttribute("institutionInfo"));
        } catch (Exception e) {} %>
        <div class="row">
            <div class="col-12 font-weight-light">
                <%= info.getName() %>
            </div>
            <% if (info.getDescription() != null) { %>
            <div class="col-12 font-weight-light">
                <%= info.getDescription() %>
            </div>
            <% }
                if (info.getLink() != null) { %>
            <div class="col-12">
                <a class="btn btn-sm btn-outline-main mx-1 my-1" href="<%=info.getLink()%>">Visit our website</a>
            </div>
            <% } %>
        </div>
    </div>
</footer>
</body>
</html>
