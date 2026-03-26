package com.horob1.doc_service.shared.exception

class AppException(val appError: AppError) : RuntimeException()
