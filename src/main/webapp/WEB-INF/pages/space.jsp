<%@ page import="top.secret.pojo.schemas.FilesMetaListResponse" %>
<%@ page import="top.secret.pojo.FileMetadata" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.ZoneOffset" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="top.secret.pojo.config.InstitutionConfig" %>
<%@ page import="top.secret.pojo.config.FilesConfig" %>
<%@ taglib prefix="c" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Main page</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.1.3/dist/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <link href="<c:url value="static/css/backgrounds.css"/>" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
</head>
<body class="d-flex flex-column h-100">
    <jsp:include page="common-header.jsp" />
    <main class="flex-grow-1 bg-main text-center h-100">
        <div class="container h-100">
            <div class="row h-100 d-flex justify-content-md-center align-items-center">
                <div class="col-10 d-flex justify-content-md-center align-items-center bg-diluted mx-2 my-2">
                    <div class="col">
                        <div class="row justify-content-md-center">
                            <div class="col">
                                <div class="row">
                                    <div class="col">
                                        <h3 class="mx-1 my-1 w-100 text-center text-truncate">Manage your files</h3>
                                    </div>
                                </div>
                                <form class="mx-0 my-0 px-0 py-0" method="POST" enctype="multipart/form-data">
                                    <div class="form-row justify-content-md-center">
                                        <div class="col-10 my-1 mr-1">
                                            <div class="input-group">
                                                <div class="input-group mb-2 input-group-lg">
                                                    <div class="input-group-prepend">
                                                        <div class="input-group-text bg-opposite">
                                                            <i class="fa fa-file"></i>
                                                        </div>
                                                    </div>
                                                    <div class="custom-file form-group form-control bg-opposite my-0 mx-0 py-0 px-0">
                                                        <input id="file-input" type="file" name="file" class="custom-file-input btn-opposite">
                                                        <label class="custom-file-label bg-opposite my-0 mx-0 pt-2 pb-0 px-0 text-truncate" for="file-input">
                                                            Click to select file
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col my-1">
                                            <button type="submit" class="w-100 btn btn-diluted btn-lg">
                                                <i class="fa fa-download"></i> Upload
                                            </button>
                                        </div>
                                    </div>
                                </form>
                                <div class="row">
                                    <div class="col mr-1 my-1">
                                        <form class="mx-0 my-0 px-0 py-0">
                                            <% InstitutionConfig info = new InstitutionConfig();
                                            int pageValue = 1;
                                            FilesConfig filesConfig = new FilesConfig();
                                            FilesMetaListResponse filesMetaResponse = new FilesMetaListResponse();
                                            try {
                                                info = ((InstitutionConfig) request.
                                                        getAttribute("institutionInfo"));
                                            } catch (Exception e) {}
                                            try {
                                                filesMetaResponse = (FilesMetaListResponse) request.
                                                        getAttribute("filesMetadata");
                                            } catch (Exception e) {}
                                            try {
                                                filesConfig = (FilesConfig) request.
                                                        getAttribute("filesConfig");
                                            } catch (Exception e) {}
                                            try {
                                                pageValue = Integer.parseInt(request.getParameter("page"));
                                            } catch (Exception e) {}
                                            if (pageValue > 2) { %>
                                            <input type="hidden" name="page" value="<%=pageValue - 1%>"/>
                                            <% } %>
                                            <button type="submit" class="w-100 btn btn-diluted btn-lg"
                                            <% if (pageValue <= 1) { %>
                                                disabled
                                            <% } %>>
                                                <i class="fa fa-backward"></i> Previous page
                                            </button>
                                        </form>
                                    </div>
                                    <div class="col mr-1 my-1">
                                        <div class="row h-100 align-items-center">
                                            <div class="col">
                                                <h4 class="text-center text-lg mx-0 my-0 px-0 py-0">
                                                    Total <%=filesMetaResponse.getTotal()%> files
                                                </h4>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col my-1">
                                        <form class="mx-0 my-0 px-0 py-0">
                                            <input type="hidden" name="page" value="<%=pageValue + 1%>" />
                                            <button type="submit" class="w-100 btn btn-diluted btn-lg"
                                                <% if (filesConfig.getPerPageLimit() * pageValue >= filesMetaResponse.getTotal()) { %>
                                                    disabled
                                                <% } %>
                                            >
                                                Next page <i class="fa fa-forward"></i>
                                            </button>
                                        </form>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col">
                                        <% Object errors = request.getAttribute("error");
                                            if (errors != null) { %>
                                        <div class="alert alert-danger w-100 mx-0 my-2" role="alert">
                                            <strong class="text-wrap"><% for (String message : (Iterable<? extends String>) errors) { %>
                                                <%=message%><br/><% } %>
                                            </strong>
                                        </div><% } %>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <% DateTimeFormatter expirationFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                                for (FileMetadata metadata : filesMetaResponse.getRecords()) {
                                    String fileExtension = "-";
                                    int dotIndex = metadata.getName().lastIndexOf('.');
                                    if (dotIndex != -1) {
                                        fileExtension = metadata.getName().substring(dotIndex + 1);
                                    }
                            %>
                            <div class="col-4">
                                <div class="card bg-opposite my-2 text text-left">
                                    <div class="card-body">
                                        <h4 class="card-title text-uppercase font-weight-bold mx-1 my-1">
                                            <%=fileExtension%>
                                        </h4>
                                        <p class="card-subtitle mx-1 my-1 text-truncate">
                                            <%=metadata.getName()%>
                                        </p>
                                        <p class="mx-1 my-1">
                                            <small class="card-text text-truncate">
                                                <p class="text-truncate"><%=metadata.getIdentifier()%></p>
                                                Added:
                                                <%=LocalDateTime.ofEpochSecond(
                                                        metadata.getCreation(), 0, ZoneOffset.ofHours(info.getTimeZoneHoursOffset())
                                                ).format(expirationFormatter)%><br>
                                                Expires:
                                                <%=LocalDateTime.ofEpochSecond(
                                                    metadata.getExpiration(), 0, ZoneOffset.ofHours(info.getTimeZoneHoursOffset())
                                                ).format(expirationFormatter)%>
                                            </small>
                                        </p>
                                        <div class="d-flex justify-content-between align-items-center">
                                            <div class="btn=group mx-1 my-1">
                                                <form class="mx-0 my-0 px-0 py-0" action="download">
                                                    <button class="btn-outline-diluted" type="button" onclick="copyValueToClipboard('<%=metadata.getIdentifier()%>')">
                                                        <i class="fa fa-copy"></i>
                                                    </button>
                                                    <input type="hidden" name="file-id" value="<%=metadata.getIdentifier()%>" />
                                                    <button class="btn-outline-diluted">
                                                        <i class="fa fa-download"></i>
                                                    </button>
                                                </form>
                                            </div>
                                            <p class="mx-1 my-1 text-wrap">
                                                ~<%=(float) Math.round(((float) metadata.getSize() / 1024) * 100) / 100%> KB
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <%
                                }
                            %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
    <jsp:include page="common-footer.jsp" />
<script type="text/javascript">
    const fileInput = document.getElementById('file-input')

    function copyValueToClipboard(value) {
        const downloadLink = window.location.protocol + "//" +
            window.location.host + "/download?file-id=" + value;
        navigator.clipboard.writeText(downloadLink);
    }

    fileInput.addEventListener('change', function() {
        fileInput.nextElementSibling.textContent = this.files[0].name;
    });
</script>
</body>
</html>
