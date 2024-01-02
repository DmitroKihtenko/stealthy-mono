<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html class="h-100">
<head>
    <title>Sign-in</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link href="<c:url value="static/css/backgrounds.css"/>" rel="stylesheet">
    <link href="<c:url value="static/css/device-sizes.css"/>" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
</head>
<body class="d-flex flex-column h-100">
<jsp:include page="common-header.jsp" />
<main class="flex-grow-1 bg-main text-center h-100">
    <div class="container h-100">
        <div class="row h-100 d-flex justify-content-md-center align-items-center">
            <div class="col-8 d-flex justify-content-md-center align-items-center bg-diluted text-lg mx-2 my-2">
                <div class="col">
                    <div class="row justify-content-md-center">
                        <div class="column">
                            <h2 class="my-1 w-100 text-center text-truncate">Server error</h2>
                        </div>
                    </div>
                    <% Object errors = request.getAttribute("error");
                        if (errors != null) { %>
                    <div class="row">
                        <div class="col my-1">
                            <div class="alert alert-danger" role="alert">
                                <strong class="text-wrap">
                                    <% for (String message : (Iterable<? extends String>) errors) { %>
                                        <%= message %><br/>
                                    <% } %>
                                </strong>
                            </div>
                        </div>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</main>
<jsp:include page="common-footer.jsp" />
</body>
</html>
