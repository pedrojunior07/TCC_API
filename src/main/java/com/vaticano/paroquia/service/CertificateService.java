package com.vaticano.paroquia.service;

import com.vaticano.paroquia.domain.enums.Role;
import com.vaticano.paroquia.dto.request.CertificateGenerateRequest;
import com.vaticano.paroquia.exception.BadRequestException;
import com.vaticano.paroquia.security.SecurityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CertificateService {

    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public GeneratedCertificate generate(CertificateGenerateRequest request) {
        securityUtils.requireAnyRole(Role.SUPER_ADMIN, Role.SECRETARIO);

        String type = normalizeType(request.getType());
        String format = normalizeFormat(request.getFormat());
        Map<String, Object> data = request.getData();

        String filenameBase = buildFilenameBase(type, data);
        byte[] content;
        String contentType;
        String extension;

        if ("pdf".equals(format)) {
            String html = buildHtml(type, data);
            content = renderPdf(html);
            contentType = "application/pdf";
            extension = "pdf";
        } else {
            content = renderWord(type, data);
            contentType = "application/msword";
            extension = "doc";
        }

        String filename = filenameBase + "." + extension;

        auditService.log(
                "certificate_generated",
                "Certificado gerado em " + format,
                null,
                request.getMemberKey()
        );

        return new GeneratedCertificate(filename, contentType, content);
    }

    private String normalizeType(String value) {
        String type = String.valueOf(value == null ? "" : value).trim().toLowerCase(Locale.ROOT);
        if ("batismo".equals(type) || "casamento".equals(type)) return type;
        throw new BadRequestException("Tipo de certificado invalido. Use: batismo ou casamento");
    }

    private String normalizeFormat(String value) {
        String format = String.valueOf(value == null ? "" : value).trim().toLowerCase(Locale.ROOT);
        if ("pdf".equals(format) || "word".equals(format) || "docx".equals(format)) {
            return "docx".equals(format) ? "word" : format;
        }
        throw new BadRequestException("Formato invalido. Use: pdf ou word");
    }

    private String buildFilenameBase(String type, Map<String, Object> data) {
        String name = value(data, "nome_baptizado");
        if (name.isBlank()) {
            String noivo = value(data, "nome_noivo");
            String noiva = value(data, "nome_noiva");
            name = (noivo + "_" + noiva).trim();
        }
        if (name.isBlank()) name = "membro";
        return "certidao_" + type + "_" + slug(name);
    }

    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception ex) {
            log.warn("Erro ao gerar PDF com logo. Tentando fallback sem logo.", ex);
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                String fallbackHtml = stripLogo(html);
                ITextRenderer renderer = new ITextRenderer();
                renderer.setDocumentFromString(fallbackHtml);
                renderer.layout();
                renderer.createPDF(outputStream);
                return outputStream.toByteArray();
            } catch (Exception fallbackEx) {
                log.error("Erro ao gerar PDF de certificado no fallback", fallbackEx);
                throw new BadRequestException("Nao foi possivel gerar o PDF do certificado");
            }
        }
    }

    private byte[] renderWord(String type, Map<String, Object> data) {
        String html = buildHtml(type, data);
        return html.getBytes(StandardCharsets.UTF_8);
    }

    private String buildHtml(String type, Map<String, Object> data) {
        String logoHtml = logoHtml();
        String headerTop = "batismo".equals(type) ? "<div class=\"subtitle\">ARQUIDIOCESE DE MAPUTO - MOCAMBIQUE</div>" : "";
        String title = "batismo".equals(type) ? "CERTIDAO DE BAPTISMO" : "CERTIDAO DE CASAMENTO";
        String body = "batismo".equals(type) ? buildBaptismBody(data) : buildMarriageBody(data);
        String wrapperClass = "batismo".equals(type) ? "page" : "page marriage";

        return "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                + "<head><meta charset=\"UTF-8\"/>"
                + "<style>"
                + "@page { size: A4; margin: 20mm 18mm 20mm 22mm; }"
                + "body { font-family: 'Times New Roman', serif; font-size: 12pt; line-height: 1.45; color: #111111; }"
                + ".page { width: 100%; }"
                + ".marriage { border-left: 2px solid #10366f; padding-left: 10mm; }"
                + ".header { text-align: center; margin-bottom: 14pt; }"
                + ".logo-wrap { text-align: center; margin-bottom: 6pt; height: 52px; }"
                + ".logo { width: 48px; height: 48px; object-fit: contain; }"
                + ".subtitle { font-size: 11.5pt; font-weight: 700; letter-spacing: 0.3px; margin-bottom: 3pt; color: #10366f; }"
                + ".title { font-size: 18pt; font-weight: 700; letter-spacing: 0.5px; margin-bottom: 10pt; color: #10366f; }"
                + "p { margin: 0 0 8pt 0; text-align: left; }"
                + ".fill { display: inline-block; border-bottom: 1px dotted #1f2937; min-width: 70px; padding: 0 3px; }"
                + ".fill.short { min-width: 48px; }"
                + ".fill.mid { min-width: 120px; }"
                + ".fill.long { min-width: 220px; }"
                + ".fill.xlong { min-width: 300px; }"
                + ".spacer { margin-top: 10pt; }"
                + ".signature { margin-top: 24pt; text-align: right; }"
                + ".sigline { display: inline-block; border-bottom: 1px dotted #1f2937; min-width: 230px; height: 18px; }"
                + "</style></head>"
                + "<body><div class=\"" + wrapperClass + "\">"
                + "<div class=\"header\">" + logoHtml + headerTop + "<div class=\"title\">" + escapeHtml(title) + "</div></div>"
                + body
                + "</div></body></html>";
    }

    private String buildMarriageBody(Map<String, Object> data) {
        DateParts parts = emissionDateParts(data);
        String noivoMae = value(data, "mae_noivo");
        String noivaMae = value(data, "mae_noiva");

        return ""
                + pRaw("Padre " + fill(value(data, "nome_oficiante"), "xlong") + " da " + fill(value(data, "paroquia"), "xlong") + ".")
                + pRaw("CERTIFICO que das folhas " + fill(value(data, "folha"), "short")
                + " sob o n. " + fill(value(data, "numero_registo"), "mid")
                + " do Livro de Registo de Casamentos celebrados nesta " + fill(value(data, "paroquia"), "long")
                + " referentes ao ano de " + fill(value(data, "ano"), "short") + ".")
                + pRaw("Consta que no dia " + fill(parts.day, "short")
                + " do mes de " + fill(parts.month, "mid")
                + " do ano de " + fill(parts.year, "short")
                + " foi celebrado o casamento canonico de " + fill(value(data, "nome_noivo"), "xlong") + ".")
                + pRaw("Filho de " + fill(value(data, "pai_noivo"), "xlong")
                + " e de " + fill(noivoMae, "xlong") + ".")
                + pRaw("Com " + fill(value(data, "nome_noiva"), "xlong")
                + ", filha de " + fill(value(data, "pai_noiva"), "xlong")
                + " e de " + fill(noivaMae, "xlong") + ".")
                + pRaw("Sendo oficiante " + fill(value(data, "nome_oficiante"), "xlong") + ".")
                + pRaw("Foram testemunhas " + fill(value(data, "nome_testemunha_1"), "xlong")
                + " e " + fill(value(data, "nome_testemunha_2"), "xlong") + ".")
                + pRaw("Por ser verdade, passo a presente certidao que assino e autentico com o "
                + fill(value(data, "autenticacao"), "mid") + ".")
                + pRaw(fill(defaultIfBlank(value(data, "local_emissao"), "Matola"), "mid")
                + ", aos " + fill(parts.day, "short")
                + " de " + fill(parts.month, "mid")
                + " de " + fill(parts.year, "short") + ".")
                + "<div class=\"signature\">"
                + "<p>O " + fill(value(data, "cargo_assinante"), "mid") + "</p>"
                + "<p><span class=\"sigline\"></span></p>"
                + "<p>" + escapeHtml(value(data, "assinante")) + "</p>"
                + "</div>";
    }

    private String buildBaptismBody(Map<String, Object> data) {
        DateParts parts = emissionDateParts(data);
        String diaBatismo = value(data, "dia_baptismo");
        String mesBatismo = value(data, "mes_baptismo");
        String anoBatismo = defaultIfBlank(value(data, "ano_registo"), parts.year);
        String dataBatismo = value(data, "data_baptismo");
        String sexo = value(data, "sexo");
        String anotacoes = value(data, "anotacoes");

        String whenBaptism;
        if (!dataBatismo.isBlank()) {
            whenBaptism = "no dia " + fill(dataBatismo, "mid");
        } else {
            whenBaptism = "no dia " + fill(diaBatismo, "short")
                    + " do mes de " + fill(mesBatismo, "mid")
                    + " do referido ano de " + fill(anoBatismo, "short");
        }

        return ""
                + pRaw("Padre " + fill(value(data, "nome_oficiante"), "xlong") + " da " + fill(value(data, "paroquia"), "xlong") + ".")
                + pRaw("CERTIFICO que as folhas " + fill(value(data, "folha"), "short")
                + " sob o n. " + fill(value(data, "numero_assento"), "mid")
                + " do Livro de Registo de Baptismos desta " + fill(value(data, "paroquia"), "long")
                + " referente ao ano de " + fill(defaultIfBlank(value(data, "ano_registo"), parts.year), "short") + ".")
                + pRaw("Consta que " + whenBaptism
                + " foi baptizado nesta " + fill(value(data, "paroquia"), "mid")
                + " um individuo do sexo " + fill(sexo, "mid")
                + " com o nome de " + fill(value(data, "nome_baptizado"), "xlong") + ".")
                + pRaw("Nascido em " + fill(value(data, "local_nascimento"), "mid")
                + ", distrito de " + fill(value(data, "distrito"), "mid")
                + ", aos " + fill(value(data, "dia_nascimento"), "short")
                + " do mes de " + fill(value(data, "mes_nascimento"), "mid")
                + " do ano de " + fill(value(data, "ano_nascimento"), "short") + ".")
                + pRaw("Filho de " + fill(value(data, "nome_pai"), "xlong")
                + ", profissao " + fill(value(data, "profissao_pai"), "mid")
                + ", natural de " + fill(value(data, "naturalidade_pai"), "mid") + ".")
                + pRaw("E de " + fill(value(data, "nome_mae"), "xlong")
                + ", profissao " + fill(value(data, "profissao_mae"), "mid")
                + ", natural de " + fill(value(data, "naturalidade_mae"), "mid") + ".")
                + pRaw("Neto paterno de " + fill(value(data, "avo_paterno"), "xlong")
                + " e materno de " + fill(value(data, "avo_materno"), "xlong") + ".")
                + pRaw("Foram padrinhos: " + fill(value(data, "nome_padrinho"), "xlong")
                + " (estado " + fill(value(data, "estado_padrinho"), "mid")
                + ", profissao " + fill(value(data, "profissao_padrinho"), "mid") + ")")
                + pRaw("E " + fill(value(data, "nome_madrinha"), "xlong")
                + " (estado " + fill(value(data, "estado_madrinha"), "mid")
                + ", profissao " + fill(value(data, "profissao_madrinha"), "mid") + ").")
                + pRaw("A margem: " + fill(anotacoes, "xlong"))
                + pRaw("Por ser verdade passo a presente Certidao que vou assinar e autenticar com o "
                + fill(value(data, "autenticacao"), "mid")
                + " em uso nesta " + fill(value(data, "paroquia"), "mid") + ".")
                + pRaw(fill(defaultIfBlank(value(data, "local_emissao"), "Matola"), "mid")
                + ", aos " + fill(parts.day, "short")
                + " de " + fill(parts.month, "mid")
                + " de " + fill(parts.year, "short") + ".")
                + "<div class=\"signature\">"
                + "<p>O " + fill(value(data, "cargo_assinante"), "mid") + "</p>"
                + "<p><span class=\"sigline\"></span></p>"
                + "<p>" + escapeHtml(value(data, "assinante")) + "</p>"
                + "</div>";
    }

    private String logoHtml() {
        String logoDataUri = resolveLogoDataUri();
        if (logoDataUri.isBlank()) return "";
        return "<div class=\"logo-wrap\"><img class=\"logo\" src=\"" + logoDataUri + "\" alt=\"Logo\" width=\"48\" height=\"48\"/></div>";
    }

    private String resolveLogoDataUri() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:static/logo*");
            if (resources.length == 0) return "";

            Resource logo = resources[0];
            byte[] bytes = logo.getInputStream().readAllBytes();
            String filename = logo.getFilename() == null ? "" : logo.getFilename().toLowerCase(Locale.ROOT);
            String mimeType = "image/png";
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) mimeType = "image/jpeg";
            if (filename.endsWith(".svg")) mimeType = "image/svg+xml";

            return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception ex) {
            log.warn("Nao foi possivel carregar logo de resources/static para certificado", ex);
            return "";
        }
    }

    private DateParts emissionDateParts(Map<String, Object> data) {
        String day = value(data, "dia");
        String month = value(data, "mes");
        String year = value(data, "ano");

        if (!day.isBlank() && !month.isBlank() && !year.isBlank()) {
            return new DateParts(day, month, year);
        }

        LocalDate now = LocalDate.now();
        String[] monthNames = {
                "Janeiro", "Fevereiro", "Marco", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        };
        return new DateParts(
                day.isBlank() ? String.valueOf(now.getDayOfMonth()) : day,
                month.isBlank() ? monthNames[now.getMonthValue() - 1] : month,
                year.isBlank() ? String.valueOf(now.getYear()) : year
        );
    }

    private String stripLogo(String html) {
        return html.replaceFirst("(?s)<div class=\\\"logo-wrap\\\">.*?</div>", "");
    }

    private String value(Map<String, Object> data, String key) {
        if (data == null) return "";
        Object raw = data.get(key);
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private String pRaw(String raw) {
        return "<p>" + raw + "</p>";
    }

    private String fill(String value, String sizeClass) {
        String classes = "fill" + (sizeClass == null || sizeClass.isBlank() ? "" : " " + sizeClass);
        String safe = escapeHtml(value);
        if (safe.isBlank()) safe = "&nbsp;";
        return "<span class=\"" + classes + "\">" + safe + "</span>";
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private String escapeHtml(String value) {
        String sanitized = sanitizeXmlText(value);
        return sanitized
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String sanitizeXmlText(String value) {
        String input = String.valueOf(value == null ? "" : value);
        StringBuilder out = new StringBuilder(input.length());
        input.codePoints().forEach(cp -> {
            if (cp == 0x9 || cp == 0xA || cp == 0xD
                    || (cp >= 0x20 && cp <= 0xD7FF)
                    || (cp >= 0xE000 && cp <= 0xFFFD)
                    || (cp >= 0x10000 && cp <= 0x10FFFF)) {
                out.appendCodePoint(cp);
            }
        });
        return out.toString().trim();
    }

    private String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+", "")
                .replaceAll("_+$", "");
        return slug.isBlank() ? "membro" : slug;
    }

    @Getter
    public static class GeneratedCertificate {
        private final String filename;
        private final String contentType;
        private final byte[] bytes;

        public GeneratedCertificate(String filename, String contentType, byte[] bytes) {
            this.filename = new String(String.valueOf(filename).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            this.contentType = contentType;
            this.bytes = bytes;
        }
    }

    private record DateParts(String day, String month, String year) {}
}
