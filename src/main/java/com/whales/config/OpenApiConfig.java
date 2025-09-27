package com.whales.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * 메인 OpenAPI 문서 설정
     * - Info (제목/버전/라이선스 등)
     * - JWT Bearer 보안 스키마
     * - 전역 Security Requirement (기본적으로 모든 엔드포인트에 적용)
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .info(new Info()
                        .title("Whales API")
                        .version("v1")
                        .description("Whales 대학 커뮤니티 백엔드 API 문서")
                        .contact(new Contact().name("Whales Team").email("team@whales.example"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                );
    }

//    /**
//     * Public API 그룹
//     * - 문서에서 /api/**, /auth/** 경로를 포함
//     */
//    @Bean
//    public GroupedOpenApi publicApi(OpenApiCustomizer securityExcludeCustomizer) {
//        return GroupedOpenApi.builder()
//                .group("public")
//                .pathsToMatch("/api/**", "/auth/**")
//                .addOpenApiCustomizer(securityExcludeCustomizer)
//                .build();
//    }
//
//    /**
//     * 보안 제외 커스텀라이저
//     * - /auth/** 경로는 전역 SecurityRequirement(=Bearer 인증) 제거
//     *   (로그인/콜백 등은 토큰 없이 접근 가능해야 하므로)
//     */
//    @Bean
//    public OpenApiCustomizer securityExcludeCustomizer() {
//        return openApi -> {
//            if (openApi.getPaths() == null) return;
//            openApi.getPaths().forEach((path, item) -> {
//                if (path.startsWith("/auth/")) {
//                    // 각 Operation에서 security 제거
//                    if (item.getGet() != null)    item.getGet().setSecurity(null);
//                    if (item.getPost() != null)   item.getPost().setSecurity(null);
//                    if (item.getPut() != null)    item.getPut().setSecurity(null);
//                    if (item.getPatch() != null)  item.getPatch().setSecurity(null);
//                    if (item.getDelete() != null) item.getDelete().setSecurity(null);
//                    if (item.getOptions() != null)item.getOptions().setSecurity(null);
//                    if (item.getHead() != null)   item.getHead().setSecurity(null);
//                    if (item.getTrace() != null)  item.getTrace().setSecurity(null);
//                }
//            });
//        };
//    }
}