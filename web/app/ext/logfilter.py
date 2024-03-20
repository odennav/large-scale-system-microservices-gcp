import logging
import socket


class AppLogFilter(logging.Filter):
    def filter(self, record):
        record.hostname = socket.gethostname()
        return True

